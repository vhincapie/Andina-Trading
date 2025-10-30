package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.AuditLogRequest;
import co.edu.unbosque.foresta.model.DTO.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequestMapping("/api/auditoria/logs")
public interface IAuditLogController {
    @PostMapping("/crear")
    ResponseEntity<Void> create(@RequestBody AuditLogRequest req, jakarta.servlet.http.HttpServletRequest http);

    @GetMapping("/buscar")
    ResponseEntity<Page<AuditLogResponse>> search(@RequestParam(required = false) String eventCode,
                                                  @RequestParam(required = false) String userId,
                                                  @RequestParam(required = false) Instant from,
                                                  @RequestParam(required = false) Instant to,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size);

    @GetMapping(value = "/export", produces = "text/csv")
    void export(@RequestParam(required = false) String eventCode,
                @RequestParam(required = false) String userId,
                @RequestParam Instant from,
                @RequestParam Instant to,
                jakarta.servlet.http.HttpServletResponse res) throws java.io.IOException;
}
