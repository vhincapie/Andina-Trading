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
import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class TransferSyncService {

    private final ITransferLogRepository repo;
    private final WebClient webClient;
    private final ObjectMapper om = new ObjectMapper();
    private final AuditSender auditSender;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;
    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;
    @Value("${alpaca.broker.transfers-url-template}")
    private String transfersUrlTemplate;

    public TransferSyncService(ITransferLogRepository repo, WebClient alpacaWebClient, AuditSender auditSender) {
        this.repo = repo;
        this.webClient = alpacaWebClient;
        this.auditSender = auditSender;
    }

    @Scheduled(fixedDelay = 60_000)
    public void sync() {
        List<TransferLog> pendientes = repo.findAllPending();
        if (pendientes.isEmpty()) {
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_SYNC_NO_PENDING",
                    "/jobs/transfer-sync",
                    "Sin transferencias pendientes",
                    Map.of()
            ));
            return;
        }

        auditSender.log("", new AuditLogRequest(
                "TRANSFER_SYNC_START",
                "/jobs/transfer-sync",
                "Inicio sincronización",
                Map.of("pendientes", pendientes.size())
        ));

        String basic = basicAuth();
        int actualizados = 0;

        for (TransferLog t : pendientes) {
            try {
                String url = transfersUrlTemplate.replace("{account_id}", t.getAlpacaAccountId());

                String body = webClient.get()
                        .uri(url)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (body == null || body.isBlank()) {
                    auditSender.log("", new AuditLogRequest(
                            "TRANSFER_SYNC_EMPTY_RESPONSE",
                            "/integracion/alpaca/transfers",
                            "Respuesta vacía",
                            Map.of("alpacaAccountId", t.getAlpacaAccountId())
                    ));
                    continue;
                }

                JsonNode arr = om.readTree(body);
                for (JsonNode node : arr) {
                    String id = node.path("id").asText(null);
                    if (id == null || !id.equals(t.getExternalId())) continue;

                    String nuevo = node.path("status").asText(null);
                    if (nuevo != null && !nuevo.equalsIgnoreCase(t.getStatus())) {
                        String anterior = t.getStatus();
                        t.setStatus(nuevo);
                        repo.save(t);
                        actualizados++;
                        auditSender.log("", new AuditLogRequest(
                                "TRANSFER_SYNC_STATUS_UPDATED",
                                "/jobs/transfer-sync",
                                "Estado actualizado",
                                Map.of("transferId", t.getExternalId(), "antes", anterior, "despues", nuevo)
                        ));
                    }
                    break;
                }
            } catch (Exception ex) {
                auditSender.log("", new AuditLogRequest(
                        "TRANSFER_SYNC_ERROR",
                        "/jobs/transfer-sync",
                        "Error sincronizando transferencia",
                        Map.of("transferId", t.getExternalId(), "mensaje", ex.getMessage())
                ));
            }
        }

        auditSender.log("", new AuditLogRequest(
                "TRANSFER_SYNC_END",
                "/jobs/transfer-sync",
                "Fin sincronización",
                Map.of("actualizados", actualizados, "procesados", pendientes.size())
        ));
    }

    private String basicAuth() {
        String creds = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }
}
