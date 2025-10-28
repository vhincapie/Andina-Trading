package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.AlpacaTradingClient;
import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.integration.InversionistasInternalClient;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.entity.Order;
import co.edu.unbosque.foresta.repository.IOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class OrderSyncService {
    private final IOrderRepository orderRepository;
    private final AlpacaTradingClient alpaca;
    private final InversionistasInternalClient inversionistasInternal;

    public OrderSyncService(IOrderRepository orderRepository,
                            AlpacaTradingClient alpaca,
                            InversionistasInternalClient inversionistasInternal) {
        this.orderRepository = orderRepository;
        this.alpaca = alpaca;
        this.inversionistasInternal = inversionistasInternal;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
    }

    @Scheduled(fixedDelay = 20_000, initialDelay = 10_000)
    @Transactional
    public void sincronizarEstadosOrdenes() {
        List<Order> pendientes = orderRepository.findOrdersToSync();
        if (pendientes.isEmpty()) {
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
                    continue;
                }

                OrderDTO remoto = alpaca.obtenerOrden(alpacaAccountId, o.getAlpacaOrderId());
                String nuevo = normalize(remoto != null ? remoto.getStatus() : null);
                String actual = normalize(o.getStatus());

                if (nuevo != null && !nuevo.equals(actual)) {
                    o.setStatus(nuevo);
                    orderRepository.save(o);
                    cambios++;
                }
            } catch (Exception e) {
            }
        }
        if (cambios > 0) {
        } else {
        }
    }

    private String obtenerAlpacaIdDeInversionista(Long inversionistaId) {
        try {
            AlpacaAccountDTO dto = inversionistasInternal.alpacaPorInversionistaId(inversionistaId);
            if (dto != null && dto.getAlpacaId() != null && !dto.getAlpacaId().isBlank()) {
                return dto.getAlpacaId();
            }
        } catch (Exception ignored) { }
        return null;
    }
}
