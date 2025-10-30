package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.AuditLogRequest;
import co.edu.unbosque.foresta.model.DTO.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

public interface IAuditLogService {
    void create(String userId, String username, String ip, AuditLogRequest req);
    Page<AuditLogResponse> search(String eventCode, String userId, Instant from, Instant to, Pageable pageable);
    void exportCsv(String eventCode, String userId, Instant from, Instant to, HttpServletResponse res) throws IOException;
}
