package co.edu.unbosque.foresta.auth.audit;

import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuditSender {

    private final WebClient auditoriaWebClient;

    public AuditSender(WebClient auditoriaWebClient) {
        this.auditoriaWebClient = auditoriaWebClient;
    }

    public void log(String bearerToken, AuditLogRequest req) {
        auditoriaWebClient.post()
                .uri("/api/auditoria/logs/crear")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(ex -> Mono.empty())
                .subscribe();
    }
}
