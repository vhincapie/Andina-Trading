package co.edu.unbosque.foresta.repository.specs;

import co.edu.unbosque.foresta.model.entity.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.Instant;

public class AuditLogSpecs {

    public static Specification<AuditLog> eventCodeEquals(String eventCode) {
        return (root, q, cb) -> eventCode == null || eventCode.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("eventCode"), eventCode);
    }

    public static Specification<AuditLog> userIdEquals(String userId) {
        return (root, q, cb) -> userId == null || userId.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("userId"), userId);
    }

    public static Specification<AuditLog> createdAtBetween(Instant from, Instant to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null)
                return cb.between(root.get("createdAt"), Timestamp.from(from), Timestamp.from(to));
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.from(from));
            return cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.from(to));
        };
    }
}
