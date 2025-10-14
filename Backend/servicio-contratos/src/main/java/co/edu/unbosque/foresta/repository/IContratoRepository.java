package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Contrato;
import co.edu.unbosque.foresta.model.enums.EstadoContratoEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IContratoRepository extends JpaRepository<Contrato, Long> {
    boolean existsByInversionistaIdAndEstado(Long inversionistaId, EstadoContratoEnum estado);
    Optional<Contrato> findFirstByInversionistaIdAndEstadoOrderByCreadoEnDesc(Long inversionistaId, EstadoContratoEnum estado);
    Optional<Contrato> findFirstByInversionistaIdAndComisionistaIdAndEstadoOrderByCreadoEnDesc(
            Long inversionistaId, Long comisionistaId, EstadoContratoEnum estado);
    List<Contrato> findByComisionistaIdOrderByFechaInicioDesc(Long comisionistaId);

}
