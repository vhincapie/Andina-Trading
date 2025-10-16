package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ITransferLogRepository extends JpaRepository<TransferLog, Long> {
    boolean existsByAlpacaAccountIdAndCreatedAtBetween(String alpacaAccountId, LocalDateTime start, LocalDateTime end);
    Optional<TransferLog> findTop1ByAlpacaAccountIdOrderByCreatedAtDesc(String alpacaAccountId);
}
