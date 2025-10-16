package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.AccountACHRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IAccountACHRepository extends JpaRepository<AccountACHRelationShip, Long> {
    List<AccountACHRelationShip> findByAlpacaAccountId(String alpacaAccountId);
    Optional<AccountACHRelationShip> findFirstByAlpacaAccountIdOrderByCreatedAtDesc(String alpacaAccountId);
}
