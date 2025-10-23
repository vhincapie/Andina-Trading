package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.AlpacaTradingClient;
import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.integration.InversionistasClient;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.entity.Order;
import co.edu.unbosque.foresta.repository.IOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderSyncService {

    private static final Logger log = LoggerFactory.getLogger(OrderSyncService.class);

    private static final Set<String> TERMINALES = Set.of(
            "FILLED","CANCELED","REJECTED","EXPIRED","DONE_FOR_DAY"
    );

    private final IOrderRepository orderRepository;
    private final AlpacaTradingClient alpaca;
    private final InversionistasClient inversionistas;

    public OrderSyncService(IOrderRepository orderRepository,
                            AlpacaTradingClient alpaca,
                            InversionistasClient inversionistas) {
        this.orderRepository = orderRepository;
        this.alpaca = alpaca;
        this.inversionistas = inversionistas;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
    }

    @Scheduled(fixedDelay = 20_000, initialDelay = 10_000)
    @Transactional
    public void sincronizarEstadosOrdenes() {
        log.info("[Sync] Tick scheduler (automático)");
        List<Order> pendientes = orderRepository.findOrdersToSync();
        if (pendientes.isEmpty()) {
            log.debug("[Sync] No hay órdenes pendientes.");
            return;
        }
        int cambios = 0;

        for (Order o : pendientes) {
            try {
                if (o.getAlpacaOrderId() == null || o.getAlpacaOrderId().isBlank()) {
                    continue;
                }

                String alpacaAccountId = obtenerAlpacaIdDeInversionista(o.getInversionistaId());
                if (alpacaAccountId == null || alpacaAccountId.isBlank()) {
                    log.debug("[Sync] Orden {} sin cuenta Alpaca", o.getId());
                    continue;
                }

                OrderDTO remoto = alpaca.obtenerOrden(alpacaAccountId, o.getAlpacaOrderId());
                String nuevo = normalize(remoto != null ? remoto.getStatus() : null);
                String actual = normalize(o.getStatus());

                if (nuevo != null && !nuevo.equals(actual)) {
                    o.setStatus(nuevo);
                    orderRepository.save(o);
                    cambios++;
                    log.info("[Sync] Orden {}: {} -> {}", o.getId(), actual, nuevo);
                }
            } catch (Exception e) {
                log.warn("[Sync] Error con orden {}: {}", o.getId(), e.getMessage());
            }
        }
        if (cambios > 0) {
            log.info("[Sync] Órdenes actualizadas: {}", cambios);
        } else {
            log.debug("[Sync] Sin cambios.");
        }
    }

    private String obtenerAlpacaIdDeInversionista(Long inversionistaId) {
        try {
            AlpacaAccountDTO dto = inversionistas.alpacaPorInversionistaId(inversionistaId);
            if (dto != null && dto.getAlpacaId() != null && !dto.getAlpacaId().isBlank()) {
                return dto.getAlpacaId();
            }
        } catch (Exception ignored) { }
        return null;
    }
}
