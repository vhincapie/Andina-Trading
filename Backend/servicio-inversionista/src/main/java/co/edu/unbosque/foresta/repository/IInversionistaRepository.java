package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Inversionista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IInversionistaRepository extends JpaRepository<Inversionista, Long> {
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<Inversionista> findByCorreoIgnoreCase(String correo);
}
