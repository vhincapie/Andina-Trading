package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Comisionista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IComisionistaRepository extends JpaRepository<Comisionista, Long> {
    Optional<Comisionista> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByNumeroDocumento(String numeroDocumento);
    Page<Comisionista> findByCorreoIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellidoIgnoreCaseContaining(String trim, String trim1, String trim2, PageRequest pr);
}
