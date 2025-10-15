package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.AlpacaAccount;
import co.edu.unbosque.foresta.model.entity.Inversionista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAlpacaAccountRepository extends JpaRepository<AlpacaAccount, Long> {
    boolean existsByInversionista(Inversionista inversionista);
}
