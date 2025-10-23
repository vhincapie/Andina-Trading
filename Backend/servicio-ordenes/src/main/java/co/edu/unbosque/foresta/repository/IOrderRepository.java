package co.edu.unbosque.foresta.repository;

import co.edu.unbosque.foresta.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByInversionistaIdOrderByCreadoEnDesc(Long inversionistaId);
    List<Order> findByComisionistaIdOrderByCreadoEnDesc(Long comisionistaId);
    List<Order> findByComisionistaIdAndStatusOrderByCreadoEnDesc(Long comisionistaId, String status);

    List<Order> findByComisionistaIdAndCreadoEnBetweenAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
            Long comisionistaId, Instant start, Instant end, String statusToExclude);

    List<Order> findByComisionistaIdAndCreadoEnGreaterThanEqualAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
            Long comisionistaId, Instant start, String statusToExclude);

    List<Order> findByComisionistaIdAndCreadoEnLessThanEqualAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
            Long comisionistaId, Instant end, String statusToExclude);

    @Query("""
           SELECT o
           FROM Order o
           WHERE o.alpacaOrderId IS NOT NULL
             AND UPPER(o.status) NOT IN ('FILLED','CANCELED','REJECTED','EXPIRED','DONE_FOR_DAY')
           ORDER BY o.creadoEn DESC
           """)
    List<Order> findOrdersToSync();

    List<Order> findByInversionistaIdAndSymbolOrderByCreadoEnDesc(Long inversionistaId, String symbol);

}
