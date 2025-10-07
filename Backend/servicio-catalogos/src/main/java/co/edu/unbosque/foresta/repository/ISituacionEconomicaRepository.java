package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.SituacionEconomica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISituacionEconomicaRepository extends JpaRepository<SituacionEconomica, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
