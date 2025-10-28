package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IFallbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class FallbackController implements IFallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @Override
    public ResponseEntity<Map<String, Object>> generic() {
        Map<String, Object> body = Map.of(
                "message", "Servicio no disponible temporalmente. Intenta más tarde.",
                "when", OffsetDateTime.now().toString()
        );
        return ResponseEntity.status(503).body(body);
    }

    @Override
    public ResponseEntity<Map<String, Object>> byService(String service, String forwardedUri, String requestId) {
        String friendly = switch (service == null ? "" : service.toLowerCase()) {
            case "trading" -> "Módulo de trading no disponible (órdenes/mercado/saldo).";
            case "cuentas" -> "Módulo de cuentas/ACH/transferencias no disponible.";
            case "contratos" -> "Módulo de contratos no disponible.";
            case "catalogos" -> "Catálogos no disponible.";
            case "inversionistas" -> "Módulo de inversionistas no disponible.";
            case "comisionistas" -> "Módulo de comisionistas no disponible.";
            case "auth", "autenticacion" -> "Autenticación no disponible.";
            default -> "Servicio no disponible temporalmente.";
        };

        Map<String, Object> body = Map.of(
                "service", service,
                "message", friendly,
                "when", OffsetDateTime.now().toString(),
                "path", forwardedUri != null ? forwardedUri : "",
                "requestId", requestId != null ? requestId : ""
        );

        log.warn("Fallback activado: service='{}', path='{}', requestId='{}'", service, forwardedUri, requestId);

        return ResponseEntity.status(503).body(body);
    }
}
