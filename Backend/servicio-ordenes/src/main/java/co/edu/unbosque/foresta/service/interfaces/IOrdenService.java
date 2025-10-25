package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;

import java.util.List;

public interface IOrdenService {
    OrderDTO crear(OrderCreateRequestDTO req);
    List<OrderDTO> misOrdenes();
    OrderDTO obtener(Long id);

    OrderDTO aprobar(Long orderId, Long comisionistaId, OrderApprovalRequestDTO req);
    OrderDTO rechazar(Long orderId, Long comisionistaId, OrderRejectRequestDTO req);
    List<OrderDTO> listarPorComisionista(Long comisionistaId, String status);
    List<OrderDTO> listarPorInversionistaParaComisionista(Long comisionistaId, Long inversionistaId);
    CommissionSummaryDTO resumenComisiones(Long comisionistaId, String from, String to);
    List<PositionDTO> listarMisPosiciones();
}
