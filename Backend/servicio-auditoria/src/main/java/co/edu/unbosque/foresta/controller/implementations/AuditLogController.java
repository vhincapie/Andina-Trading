package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IAuditLogController;
import co.edu.unbosque.foresta.model.DTO.AuditLogRequest;
import co.edu.unbosque.foresta.model.DTO.AuditLogResponse;
import co.edu.unbosque.foresta.security.JwtService;
import co.edu.unbosque.foresta.service.interfaces.IAuditLogService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class AuditLogController implements IAuditLogController {

    private final IAuditLogService service;
    private final JwtService jwtService;

    public AuditLogController(IAuditLogService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'INVERSIONISTA')")
    public ResponseEntity<Void> create(AuditLogRequest req, HttpServletRequest http) {
        String auth = http.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : null;
        String userId = null;
        String username = null;
        if (token != null) {
            Jws<Claims> jws = jwtService.parse(token);
            Claims c = jws.getPayload();
            userId = c.getSubject();
            String preferred = c.get("preferred_username", String.class);
            username = preferred != null ? preferred : c.get("username", String.class);
            if (username == null) username = userId;
        }
        String ip = http.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = http.getRemoteAddr();
        service.create(userId, username, ip, req);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> search(String eventCode, String userId, Instant from, Instant to, int page, int size) {
        Page<AuditLogResponse> data = service.search(eventCode, userId, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(data);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void export(String eventCode, String userId, Instant from, Instant to, HttpServletResponse res) throws java.io.IOException {
        service.exportCsv(eventCode, userId, from, to, res);
    }
}
