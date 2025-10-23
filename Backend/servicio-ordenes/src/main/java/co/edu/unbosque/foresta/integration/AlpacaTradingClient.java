package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.model.DTO.OrderCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.PositionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class AlpacaTradingClient {

    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.trading.orders-url}")
    private String ordersBaseUrl;

    public AlpacaTradingClient(RestTemplate rest) {
        this.rest = rest;
    }

    public OrderDTO crearOrden(String alpacaAccountId, OrderCreateRequestDTO req) {
        String url = ordersBaseUrl + "/" + alpacaAccountId + "/orders";

        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> res = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(req, h),
                    String.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BadRequestException("Alpaca respondió " + res.getStatusCode().value());
            }

            JsonNode n = mapper.readTree(res.getBody());

            OrderDTO out = new OrderDTO();
            out.setId(getText(n, "id"));
            out.setAlpacaOrderId(getText(n, "id"));
            out.setStatus(getText(n, "status"));

            out.setSymbol(getText(n, "symbol"));
            out.setQty(getText(n, "qty"));
            out.setOrderType(getText(n, "type"));
            out.setSide(getText(n, "side"));
            out.setTimeInForce(getText(n, "time_in_force"));

            out.setLimitPrice(getText(n, "limit_price"));
            out.setStopPrice(getText(n, "stop_price"));

            return out;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String body = e.getResponseBodyAsString();
            int code = e.getStatusCode().value();
            String msg = (body != null && !body.isBlank())
                    ? "Alpaca (" + code + "): " + body
                    : "Alpaca (" + code + ")";
            throw new BadRequestException(msg);
        } catch (Exception e) {
            throw new BadRequestException("Error al llamar Alpaca: " + e.getMessage());
        }
    }

    private static String getText(JsonNode n, String field) {
        return (n != null && n.hasNonNull(field)) ? n.get(field).asText() : null;
    }

    public OrderDTO obtenerOrden(String alpacaAccountId, String alpacaOrderId) {
        String url = ordersBaseUrl + "/" + alpacaAccountId + "/orders/" + alpacaOrderId;
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<OrderDTO> res =
                rest.exchange(url, HttpMethod.GET, new HttpEntity<>(h), OrderDTO.class);
        return res.getBody();
    }

    public List<PositionDTO> listarPosiciones(String alpacaAccountId) {
        String url = ordersBaseUrl + "/" + alpacaAccountId + "/positions";
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(h);
        ResponseEntity<PositionDTO[]> res =
                rest.exchange(url, HttpMethod.GET, entity, PositionDTO[].class);
        PositionDTO[] arr = res.getBody();
        return arr != null ? Arrays.asList(arr) : List.of();
    }
}
