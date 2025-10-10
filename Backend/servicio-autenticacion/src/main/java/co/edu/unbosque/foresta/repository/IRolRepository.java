package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Rol;
import co.edu.unbosque.foresta.model.enums.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolEnum nombre);
}
