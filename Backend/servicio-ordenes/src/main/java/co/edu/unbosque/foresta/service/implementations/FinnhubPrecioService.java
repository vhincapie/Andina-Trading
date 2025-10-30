package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.service.interfaces.IPrecioService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class FinnhubPrecioService implements IPrecioService {

    private final RestTemplate rest;
    private final AuditSender auditSender;
    @Value("${finnhub.api.key}") private String apiKey;

    public FinnhubPrecioService(RestTemplate rest, AuditSender auditSender) { this.rest = rest; this.auditSender = auditSender; }

    @Override
    public double obtenerPrecioActual(String symbol) {
        try {
            String url = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=" + apiKey;
            ResponseEntity<String> res = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class, symbol);
            String body = res.getBody();
            double current = new JSONObject(body == null ? "{}" : body).optDouble("c", 0.0);
            if (current <= 0.0) {
                auditSender.log("", new AuditLogRequest(
                        "FINNHUB_PRICE_INVALID",
                        "/integracion/finnhub/quote",
                        "Precio inválido",
                        Map.of("symbol", symbol)
                ));
                throw new BadRequestException("Precio inválido para " + symbol);
            }
            auditSender.log("", new AuditLogRequest(
                    "FINNHUB_PRICE_OK",
                    "/integracion/finnhub/quote",
                    "Precio consultado",
                    Map.of("symbol", symbol, "price", current)
            ));
            return current;
        } catch (Exception e) {
            auditSender.log("", new AuditLogRequest(
                    "FINNHUB_PRICE_ERROR",
                    "/integracion/finnhub/quote",
                    "Error consultando precio",
                    Map.of("symbol", symbol)
            ));
            throw new BadRequestException("No se pudo obtener precio para " + symbol);
        }
    }
}
