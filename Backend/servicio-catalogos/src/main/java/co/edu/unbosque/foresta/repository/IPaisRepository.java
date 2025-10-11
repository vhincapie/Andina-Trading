package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaisRepository extends JpaRepository<Pais, Long> {
    boolean existsByCodigoIso3IgnoreCase(String codigoIso3);
    boolean existsByNombreIgnoreCase(String nombre);
}
