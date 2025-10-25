package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IOrdenesComisionistaController;
import co.edu.unbosque.foresta.integration.ComisionistasClient;
import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.model.DTO.CommissionSummaryDTO;
import co.edu.unbosque.foresta.model.DTO.OrderApprovalRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.OrderRejectRequestDTO;
import co.edu.unbosque.foresta.service.interfaces.IOrdenService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrdenesComisionistaController implements IOrdenesComisionistaController {

    private final IOrdenService service;
    private final ComisionistasClient comisionistasClient;

    public OrdenesComisionistaController(IOrdenService service, ComisionistasClient comisionistasClient) {
        this.service = service;
        this.comisionistasClient = comisionistasClient;
    }

    private Long comisionistaIdActual() {
        ComisionistaDTO me = comisionistasClient.miPerfil();
        if (me == null || me.getId() == null) throw new RuntimeException("No fue posible resolver el comisionista actual");
        return me.getId();
    }

    @Override
    public List<OrderDTO> misOrdenes(String status) {
        return service.listarPorComisionista(comisionistaIdActual(), status);
    }

    @Override
    public List<OrderDTO> porInversionista(Long inversionistaId) {
        return service.listarPorInversionistaParaComisionista(comisionistaIdActual(), inversionistaId);
    }

    @Override
    public OrderDTO aprobar(Long id, OrderApprovalRequestDTO req) {
        return service.aprobar(id, comisionistaIdActual(), req);
    }

    @Override
    public OrderDTO rechazar(Long id, OrderRejectRequestDTO req) {
        return service.rechazar(id, comisionistaIdActual(), req);
    }

    @Override
    public CommissionSummaryDTO resumen(String from, String to) {
        return service.resumenComisiones(comisionistaIdActual(), from, to);
    }
}
