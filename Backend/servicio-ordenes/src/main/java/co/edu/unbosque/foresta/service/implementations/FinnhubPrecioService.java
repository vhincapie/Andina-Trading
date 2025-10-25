package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.service.interfaces.IPrecioService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FinnhubPrecioService implements IPrecioService {

    private final RestTemplate rest;
    @Value("${finnhub.api.key}") private String apiKey;

    public FinnhubPrecioService(RestTemplate rest) { this.rest = rest; }

    @Override
    public double obtenerPrecioActual(String symbol) {
        try {
            String url = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=" + apiKey;
            ResponseEntity<String> res = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class, symbol);
            String body = res.getBody();
            double current = new JSONObject(body).optDouble("c", 0.0);
            if (current <= 0.0) throw new BadRequestException("Precio inválido para " + symbol);
            return current;
        } catch (Exception e) {
            throw new BadRequestException("No se pudo obtener precio para " + symbol);
        }
    }
}
