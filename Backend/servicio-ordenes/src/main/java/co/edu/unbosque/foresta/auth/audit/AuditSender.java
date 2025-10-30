package co.edu.unbosque.foresta.auth.audit;

import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;
import co.edu.unbosque.foresta.auth.security.BearerResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuditSender {

    private static final Logger log = LoggerFactory.getLogger(AuditSender.class);

    private final WebClient auditoriaWebClient;
    private final BearerResolver bearerResolver;

    public AuditSender(WebClient auditoriaWebClient, BearerResolver bearerResolver) {
        this.auditoriaWebClient = auditoriaWebClient;
        this.bearerResolver = bearerResolver;
    }

   public void log(String bearerToken, AuditLogRequest req) {
        String bearer = (bearerToken != null && !bearerToken.isBlank())
                ? bearerToken
                : bearerResolver.currentBearerOrNull();

        auditoriaWebClient.post()
                .uri("/api/auditoria/logs/crear")
                .headers(h -> { if (bearer != null) h.set(HttpHeaders.AUTHORIZATION, bearer); })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(ex -> log.warn("Audit log falló: {}", ex.toString()))
                .onErrorResume(ex -> Mono.empty())
                .subscribe();
    }
}
