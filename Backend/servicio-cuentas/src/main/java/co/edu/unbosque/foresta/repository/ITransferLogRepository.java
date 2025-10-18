package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITransferLogRepository extends JpaRepository<TransferLog, Long> {
    boolean existsByAlpacaAccountIdAndCreatedAtBetween(String alpacaAccountId, LocalDateTime start, LocalDateTime end);
    Optional<TransferLog> findTop1ByAlpacaAccountIdOrderByCreatedAtDesc(String alpacaAccountId);

    @Query("""
           SELECT t FROM TransferLog t
           WHERE (t.status IS NULL OR UPPER(t.status) <> 'COMPLETE')
           ORDER BY t.createdAt DESC
           """)
    List<TransferLog> findAllPending();
}
