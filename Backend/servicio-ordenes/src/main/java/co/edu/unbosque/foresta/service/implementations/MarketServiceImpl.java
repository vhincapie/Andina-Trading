package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.MarketStatusDTO;
import co.edu.unbosque.foresta.model.DTO.StockDTO;
import co.edu.unbosque.foresta.service.interfaces.IMarketService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketServiceImpl implements IMarketService {

    private static final Logger logger = LoggerFactory.getLogger(MarketServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${finnhub.api.key}")
    private String apiKey;

    private static final String SYMBOL_LOOKUP_URL = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=";
    private static final String SYMBOL_SEARCH_URL = "https://finnhub.io/api/v1/search?q=";
    private static final String SYMBOL_QUOTE_URL  = "https://finnhub.io/api/v1/quote?symbol=";

    public MarketServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public MarketStatusDTO getMarketStatus() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        boolean businessDay = now.getDayOfWeek() != DayOfWeek.SATURDAY && now.getDayOfWeek() != DayOfWeek.SUNDAY;
        LocalTime t = now.toLocalTime();
        boolean open = businessDay && !t.isBefore(LocalTime.of(9,30)) && t.isBefore(LocalTime.of(16,0));
        return new MarketStatusDTO(open, open ? "El mercado está abierto" : "El mercado está cerrado");
    }

    @Override
    public StockDTO getQuote(String symbol) {
        String url = SYMBOL_QUOTE_URL + symbol + "&token=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        if (response == null || response.isBlank() || "{}".equals(response)) {
            throw new RuntimeException("Sin datos para símbolo: " + symbol);
        }
        JSONObject json = new JSONObject(response);

        double c  = json.optDouble("c", 0.0);
        double h  = json.optDouble("h", 0.0);
        double l  = json.optDouble("l", 0.0);
        double pc = json.optDouble("pc", 0.0);

        return new StockDTO(
                symbol.toUpperCase(),
                "Cotización de " + symbol.toUpperCase(),
                c, h, l, pc,
                System.currentTimeMillis() / 1000
        );
    }

    @Override
    public List<StockDTO> searchSymbols(String query) {
        logger.debug("Busqueda de simbolos iniciada con query: {}", query);
        List<StockDTO> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        suggestions.addAll(buscarPorNombre(query));
        if (!suggestions.isEmpty()) return suggestions;

        suggestions.addAll(buscarPorMic(lowerQuery));
        return suggestions;
    }

    private List<StockDTO> buscarPorNombre(String query) {
        List<StockDTO> resultados = new ArrayList<>();
        try {
            logger.debug("Buscando por nombre/símbolo: {}", query);
            String searchUrl = SYMBOL_SEARCH_URL + query + "&token=" + apiKey;
            String response = restTemplate.getForObject(searchUrl, String.class);
            JSONArray results = new JSONObject(response).optJSONArray("result");
            if (results == null) return resultados;

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String symbol = item.optString("symbol", "");
                String description = item.optString("description", "");

                if (!symbol.contains(".")) {
                    StockDTO dto = buildStockDTO(symbol, description);
                    if (dto != null && dto.getCurrentPrice() > 0.0) resultados.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error al buscar símbolos por nombre: {}", e.getMessage());
            throw new RuntimeException("Error al buscar símbolo en Finnhub: " + e.getMessage());
        }
        return resultados;
    }

    private List<StockDTO> buscarPorMic(String lowerQuery) {
        List<StockDTO> resultados = new ArrayList<>();
        try {
            logger.debug("Buscando por MIC con query: {}", lowerQuery);
            String response = restTemplate.getForObject(SYMBOL_LOOKUP_URL + apiKey, String.class);
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String symbol = item.optString("symbol", "");
                String description = item.optString("description", "");
                String mic = item.optString("mic", "");

                if (!symbol.contains(".")
                        && (description.toLowerCase().contains(lowerQuery)
                        || symbol.toLowerCase().contains(lowerQuery))
                        && (mic.equalsIgnoreCase("XNYS") || mic.equalsIgnoreCase("XNAS"))) {

                    StockDTO dto = buildStockDTO(symbol, description);
                    if (dto != null) resultados.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error al buscar símbolos por MIC: {}", e.getMessage());
            throw new RuntimeException("No se pudieron buscar símbolos por MIC: " + e.getMessage());
        }
        return resultados;
    }

    private StockDTO buildStockDTO(String symbol, String description) {
        try {
            String quoteUrl = SYMBOL_QUOTE_URL + symbol + "&token=" + apiKey;
            String quoteResponse = restTemplate.getForObject(quoteUrl, String.class);
            JSONObject q = new JSONObject(quoteResponse);

            double c  = q.optDouble("c", 0.0);
            double h  = q.optDouble("h", 0.0);
            double l  = q.optDouble("l", 0.0);
            double pc = q.optDouble("pc", 0.0);

            StockDTO dto = new StockDTO();
            dto.setSymbol(symbol);
            dto.setDescription(description);
            dto.setCurrentPrice(c);
            dto.setHighPrice(h);
            dto.setLowPrice(l);
            dto.setPreviousClosePrice(pc);
            dto.setTimestamp(System.currentTimeMillis() / 1000);
            return dto;
        } catch (Exception e) {
            logger.warn("No se pudo obtener cotización para {}: {}", symbol, e.getMessage());
            return null;
        }
    }
}
