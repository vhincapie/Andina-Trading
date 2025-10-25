package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.CommissionSummaryDTO;
import co.edu.unbosque.foresta.model.DTO.OrderApprovalRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.OrderRejectRequestDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/ordenes/comisionista")
public interface IOrdenesComisionistaController {

    @GetMapping("/mis-ordenes")
    @PreAuthorize("hasRole('COMISIONISTA')")
    List<OrderDTO> misOrdenes(@RequestParam(value="status", required=false) String status);

    @GetMapping("/inversionistas/{inversionistaId}")
    @PreAuthorize("hasRole('COMISIONISTA')")
    List<OrderDTO> porInversionista(@PathVariable Long inversionistaId);

    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('COMISIONISTA')")
    OrderDTO aprobar(@PathVariable Long id, @RequestBody(required=false) OrderApprovalRequestDTO req);

    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('COMISIONISTA')")
    OrderDTO rechazar(@PathVariable Long id, @RequestBody OrderRejectRequestDTO req);

    @GetMapping("/comisiones/resumen")
    @PreAuthorize("hasRole('COMISIONISTA')")
    CommissionSummaryDTO resumen(@RequestParam(required=false) String from,
                                 @RequestParam(required=false) String to);
}
