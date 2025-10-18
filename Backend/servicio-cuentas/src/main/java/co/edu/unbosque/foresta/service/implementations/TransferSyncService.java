package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.entity.TransferLog;
import co.edu.unbosque.foresta.repository.ITransferLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class TransferSyncService {

    private final ITransferLogRepository repo;
    private final WebClient webClient;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${alpaca.broker.api.key}")
    private String apiKey;
    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;
    @Value("${alpaca.broker.transfers-url-template}")
    private String transfersUrlTemplate;

    public TransferSyncService(ITransferLogRepository repo, WebClient alpacaWebClient) {
        this.repo = repo;
        this.webClient = alpacaWebClient;
    }

    @Scheduled(fixedDelay = 60_000)
    public void sync() {
        List<TransferLog> pendientes = repo.findAllPending();
        if (pendientes.isEmpty()) return;

        String basic = basicAuth();

        for (TransferLog t : pendientes) {
            try {
                String url = transfersUrlTemplate.replace("{account_id}", t.getAlpacaAccountId());

                String body = webClient.get()
                        .uri(url)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                if (body == null || body.isBlank()) continue;

                JsonNode arr = om.readTree(body);
                for (JsonNode node : arr) {
                    String id = node.path("id").asText(null);
                    if (id == null || !id.equals(t.getExternalId())) continue;

                    String nuevo = node.path("status").asText(null);
                    if (nuevo != null && !nuevo.equalsIgnoreCase(t.getStatus())) {
                        t.setStatus(nuevo);
                        repo.save(t);
                    }
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String basicAuth() {
        String creds = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }
}
