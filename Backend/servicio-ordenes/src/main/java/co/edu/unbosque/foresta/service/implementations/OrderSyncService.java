package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.AlpacaTradingClient;
import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.integration.InversionistasInternalClient;
import co.edu.unbosque.foresta.integration.NotificacionesClient;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.entity.Order;
import co.edu.unbosque.foresta.repository.IOrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class OrderSyncService {

    private final IOrderRepository orderRepository;
    private final AlpacaTradingClient alpaca;
    private final NotificacionesClient notificacionesClient;
    private final InversionistasInternalClient inversionistasInternal;

    public OrderSyncService(IOrderRepository orderRepository,
                            AlpacaTradingClient alpaca,
                            NotificacionesClient notificacionesClient,
                            InversionistasInternalClient inversionistasInternal) {
        this.orderRepository = orderRepository;
        this.alpaca = alpaca;
        this.notificacionesClient = notificacionesClient;
        this.inversionistasInternal = inversionistasInternal;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
    }

    @Scheduled(fixedDelay = 20_000, initialDelay = 10_000)
    @Transactional
    public void sincronizarEstadosOrdenes() {
        reintentarNotificacionesPendientes();

        List<Order> pendientes = orderRepository.findOrdersToSync();
        if (pendientes.isEmpty()) return;

        for (Order o : pendientes) {
            try {
                if (o.getAlpacaOrderId() == null || o.getAlpacaOrderId().isBlank()) continue;

                String alpacaAccountId = obtenerAlpacaIdDeInversionista(o.getInversionistaId());
                if (alpacaAccountId == null || alpacaAccountId.isBlank()) continue;

                OrderDTO remoto = alpaca.obtenerOrden(alpacaAccountId, o.getAlpacaOrderId());
                String nuevo = normalize(remoto != null ? remoto.getStatus() : null);
                String actual = normalize(o.getStatus());

                if (nuevo != null && !nuevo.equals(actual)) {
                    o.setStatus(nuevo);
                    orderRepository.save(o);

                    if ("FILLED".equalsIgnoreCase(nuevo) && o.getFilledNotifiedAt() == null) {
                        try {
                            enviarNotificacionFilled(o);
                            o.setFilledNotifiedAt(java.time.Instant.now());
                            orderRepository.save(o);
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void reintentarNotificacionesPendientes() {
        List<Order> sinNotif = orderRepository.findFilledWithoutNotification();
        for (Order o : sinNotif) {
            try {
                enviarNotificacionFilled(o);
                o.setFilledNotifiedAt(java.time.Instant.now());
                orderRepository.save(o);
            } catch (Exception ignored) {}
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

    private void enviarNotificacionFilled(Order o) {
        String nombre = null, correo = null;
        try {
            var inv = inversionistasInternal.obtenerPorId(o.getInversionistaId());
            if (inv != null) {
                nombre = ((inv.getNombre() != null ? inv.getNombre() : "") + " " +
                        (inv.getApellido() != null ? inv.getApellido() : "")).trim();
                correo = inv.getCorreo();
            }
        } catch (Exception ignored) { }

        if (correo == null || correo.isBlank()) return;

        var req = new co.edu.unbosque.foresta.integration.DTO.OrdenFilledEmailRequest();
        req.setOrderDbId(o.getId());
        req.setSymbol(o.getSymbol());
        req.setSide(o.getSide());
        req.setQty(o.getQty() != null ? o.getQty().toPlainString() : null);
        req.setUnitPrice(o.getUnitPrice());
        req.setNetAmount(o.getNetAmount());
        req.setMoneda(o.getMoneda());
        req.setFilledAt(o.getActualizadoEn());
        req.setInversionistaNombre(nombre);
        req.setInversionistaCorreo(correo);

        try {
            notificacionesClient.notificarOrdenFilled(req);
            o.setFilledNotifiedAt(java.time.Instant.now());
            orderRepository.save(o);
        } catch (Exception ignored) { }
    }
}
