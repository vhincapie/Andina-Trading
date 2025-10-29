package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.external.AlpacaBarsResponse;
import co.edu.unbosque.foresta.model.DTO.CandleDTO;
import co.edu.unbosque.foresta.service.interfaces.IMarketDataService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MarketDataServiceImpl implements IMarketDataService {

    @Value("${alpaca.market.base-url}")
    private String baseUrl;

    @Value("${marketdata.default.feed:iex}")
    private String defaultFeed;

    @Value("${marketdata.default.watchlist:AAPL,MSFT,NVDA}")
    private String defaultSymbols;

    private final RestTemplate alpacaClient;

    public MarketDataServiceImpl(@Qualifier("alpacaMarketClient") RestTemplate alpacaClient) {
        this.alpacaClient = alpacaClient;
    }

    @Override
    public Map<String, List<CandleDTO>> getCandles(
            List<String> symbols, String timeframe, String feed,
            String startIso, String endIso, Integer limit
    ) {
        String tf = (timeframe == null || timeframe.isBlank()) ? "1Day" : timeframe;
        String fd = (feed == null || feed.isBlank()) ? defaultFeed : feed;
        int lim = (limit == null || limit <= 0) ? 1000 : limit;

        List<String> syms = (symbols == null || symbols.isEmpty())
                ? Arrays.asList(defaultSymbols.split("\\s*,\\s*"))
                : symbols;

        Instant end = (endIso == null || endIso.isBlank()) ? Instant.now() : Instant.parse(endIso);
        Instant start = (startIso == null || startIso.isBlank())
                ? end.minus(10, ChronoUnit.DAYS)
                : Instant.parse(startIso);

        Map<String, List<CandleDTO>> result = new LinkedHashMap<>();
        String pageToken = null;

        do {
            String url = baseUrl + UriComponentsBuilder
                    .fromPath("/v2/stocks/bars")
                    .queryParam("symbols", String.join(",", syms))
                    .queryParam("timeframe", tf)
                    .queryParam("feed", fd)
                    .queryParam("limit", lim)
                    .queryParam("start", start.toString())
                    .queryParam("end", end.toString())
                    .queryParamIfPresent("page_token", Optional.ofNullable(pageToken))
                    .build(true)
                    .toUriString();

            ResponseEntity<AlpacaBarsResponse> resp = alpacaClient.exchange(
                    url, HttpMethod.GET, HttpEntity.EMPTY, AlpacaBarsResponse.class
            );

            AlpacaBarsResponse body = resp.getBody();
            if (body == null || body.getBars() == null || body.getBars().isNull()) break;

            JsonNode bars = body.getBars();
            if (bars.isObject()) {
                bars.fields().forEachRemaining(entry -> {
                    String sym = entry.getKey();
                    JsonNode arr = entry.getValue();

                    if (arr.isArray()) {
                        for (JsonNode b : arr) {
                            result.computeIfAbsent(sym, k -> new ArrayList<>())
                                    .add(new CandleDTO(
                                            b.path("t").asText(),
                                            b.path("o").asDouble(),
                                            b.path("h").asDouble(),
                                            b.path("l").asDouble(),
                                            b.path("c").asDouble(),
                                            b.path("v").asLong()
                                    ));
                        }
                    }
                });
            }

            pageToken = body.getNext_page_token();

        } while (pageToken != null);

        result.values().forEach(list -> list.sort(Comparator.comparing(CandleDTO::getTime)));

        return result;
    }
}