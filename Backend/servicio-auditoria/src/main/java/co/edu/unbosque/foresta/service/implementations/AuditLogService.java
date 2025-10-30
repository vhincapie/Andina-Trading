package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.AuditLogRequest;
import co.edu.unbosque.foresta.model.DTO.AuditLogResponse;
import co.edu.unbosque.foresta.model.entity.AuditLog;
import co.edu.unbosque.foresta.repository.IAuditLogRepository;
import co.edu.unbosque.foresta.repository.specs.AuditLogSpecs;
import co.edu.unbosque.foresta.service.interfaces.IAuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditLogService implements IAuditLogService {

    private final IAuditLogRepository repo;
    private final ModelMapper mm;
    private final ObjectMapper om;

    public AuditLogService(IAuditLogRepository repo, ModelMapper mm, ObjectMapper om) {
        this.repo = repo;
        this.mm = mm;
        this.om = om;
    }

    @Override
    public void create(String userId, String username, String ip, AuditLogRequest req) {
        AuditLog e = new AuditLog();
        e.setEventCode(req.getEventCode());
        e.setUserId(userId);
        e.setUsername(username);
        e.setIpAddress(ip);
        e.setResource(req.getResource());
        e.setAction(req.getAction());
        try {
            e.setDetailsJson(req.getDetails() != null ? om.writeValueAsString(req.getDetails()) : "{}");
        } catch (Exception ex) {
            e.setDetailsJson("{}");
        }
        e.setCreatedAt(Timestamp.from(Instant.now()));
        repo.save(e);
    }

    @Override
    public Page<AuditLogResponse> search(String eventCode, String userId, Instant from, Instant to, Pageable pageable) {
        Specification<AuditLog> spec = Specification.allOf(
                AuditLogSpecs.eventCodeEquals(eventCode),
                AuditLogSpecs.userIdEquals(userId),
                AuditLogSpecs.createdAtBetween(from, to)
        );
        return repo.findAll(spec, pageable).map(l -> mm.map(l, AuditLogResponse.class));
    }

    @Override
    public void exportCsv(String eventCode, String userId, Instant from, Instant to, HttpServletResponse res) throws IOException {
        res.setHeader("Content-Disposition", "attachment; filename=audit-logs-" +
                Instant.now().toString().replace(":", "-") + ".csv");
        res.setContentType("text/csv;charset=UTF-8");

        Specification<AuditLog> spec = Specification.allOf(
                AuditLogSpecs.eventCodeEquals(eventCode),
                AuditLogSpecs.userIdEquals(userId),
                AuditLogSpecs.createdAtBetween(from, to)
        );

        List<AuditLog> all = repo.findAll(spec);

        LinkedHashSet<String> detailKeys = new LinkedHashSet<>();
        for (AuditLog l : all) {
            Map<String, Object> map = safeJsonToMap(l.getDetailsJson());
            detailKeys.addAll(map.keySet());
        }

        PrintWriter w = res.getWriter();
        w.write("\uFEFF");
        final String SEP = ";";

        w.println("sep=" + SEP);
        List<String> baseHeaders = List.of(
                "ID", "Fecha", "Event Code", "User ID", "Username", "IP", "Recurso", "Acción"
        );
        String header = String.join(SEP, baseHeaders) +
                (detailKeys.isEmpty() ? "" :
                        SEP + detailKeys.stream().map(k -> "details_" + k).collect(Collectors.joining(SEP)));
        w.println(header);

        ZoneId tz = ZoneId.of("America/Bogota");
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(tz);

        for (AuditLog l : all) {
            Map<String, Object> map = safeJsonToMap(l.getDetailsJson());

            List<String> base = List.of(
                    cell(l.getId()),
                    cell(l.getCreatedAt() != null ? FMT.format(l.getCreatedAt().toInstant()) : ""),
                    cell(l.getEventCode()),
                    cell(l.getUserId()),
                    cell(l.getUsername()),
                    cell(l.getIpAddress()),
                    cell(l.getResource()),
                    cell(l.getAction())
            );

            StringBuilder row = new StringBuilder(String.join(SEP, base));

            if (!detailKeys.isEmpty()) {
                for (String k : detailKeys) {
                    Object v = map.get(k);
                    row.append(SEP).append(cell(v));
                }
            }
            w.println(row.toString());
        }
        w.flush();
    }

    private Map<String, Object> safeJsonToMap(String json) {
        try {
            if (json == null || json.isBlank()) return java.util.Collections.emptyMap();
            return om.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return java.util.Map.of("raw", json);
        }
    }

    private String cell(Object o) {
        String s = (o == null) ? "" : String.valueOf(o);
        boolean mustQuote = s.contains(";") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        s = s.replace("\"", "\"\"");
        return mustQuote ? ("\"" + s + "\"") : s;
    }

    private String nz(String s) { return s == null ? "" : s.replace("\"", "\"\""); }
    private String csv(String s) { return s == null ? "{}" : s.replace("\"", "\"\""); }
}
