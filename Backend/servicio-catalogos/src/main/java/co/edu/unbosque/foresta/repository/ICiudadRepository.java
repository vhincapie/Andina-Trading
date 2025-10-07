package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICiudadRepository extends JpaRepository<Ciudad, Long> {
   boolean existsByPaisIdAndNombreIgnoreCase(Long paisId, String nombre);
}