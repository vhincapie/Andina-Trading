// src/main/java/co/edu/unbosque/foresta/integration/AlpacaTradingClient.java
package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.model.DTO.OrderCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.PositionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Arrays;
import java.util.List;

@Component
public class AlpacaTradingClient {
    private final RestTemplate rest;
    @Value("${alpaca.broker.api.key}")    private String apiKey;
    @Value("${alpaca.broker.api.secret}") private String apiSecret;
    @Value("${alpaca.trading.orders-url}") private String ordersBaseUrl;

    public AlpacaTradingClient(@Qualifier("externalRestTemplate") RestTemplate rest) {
        this.rest = rest;
    }

    private HttpHeaders alpacaHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.set("APCA-API-KEY-ID", apiKey);
        h.set("APCA-API-SECRET-KEY", apiSecret);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }

    public List<PositionDTO> listarPosiciones(String accountId) {
        String url = String.format("%s/%s/positions", ordersBaseUrl, accountId);
        try {
            HttpEntity<Void> entity = new HttpEntity<>(alpacaHeaders());
            ResponseEntity<PositionDTO[]> res = rest.exchange(url, HttpMethod.GET, entity, PositionDTO[].class);
            return res.getBody() != null ? Arrays.asList(res.getBody()) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String body = e.getResponseBodyAsString();
            throw new BadRequestException("Alpaca (" + e.getStatusCode().value() + "): " + body);
        } catch (Exception e) {
            throw new BadRequestException("Error llamando Alpaca posiciones: " + e.getMessage());
        }
    }


    public OrderDTO crearOrden(String accountId, OrderCreateRequestDTO req) {
        String url = String.format("%s/%s/orders", ordersBaseUrl, accountId);
        try {
            HttpHeaders h = alpacaHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<OrderDTO> res = rest.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(req, h), OrderDTO.class
            );
            return res.getBody();
        } catch (RestClientResponseException e) {
            throw new BadRequestException("Alpaca (" + e.getRawStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BadRequestException("Error creando orden Alpaca: " + e.getMessage());
        }
    }

    public OrderDTO obtenerOrden(String accountId, String alpacaOrderId) {
        String url = String.format("%s/%s/orders/%s", ordersBaseUrl, accountId, alpacaOrderId);
        try {
            ResponseEntity<OrderDTO> res = rest.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(alpacaHeaders()), OrderDTO.class
            );
            return res.getBody();
        } catch (RestClientResponseException e) {
            throw new BadRequestException("Alpaca (" + e.getRawStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BadRequestException("Error consultando orden Alpaca: " + e.getMessage());
        }
    }
}
