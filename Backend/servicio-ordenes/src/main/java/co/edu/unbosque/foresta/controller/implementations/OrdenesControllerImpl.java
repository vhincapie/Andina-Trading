package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IOrdenesController;
import co.edu.unbosque.foresta.model.DTO.OrderCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.PositionDTO;
import co.edu.unbosque.foresta.service.interfaces.IOrdenService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrdenesControllerImpl implements IOrdenesController {

    private final IOrdenService service;

    public OrdenesControllerImpl(IOrdenService service) {
        this.service = service;
    }

    @Override
    public OrderDTO crear(OrderCreateRequestDTO req) { return service.crear(req); }

    @Override
    public List<OrderDTO> misOrdenes() { return service.misOrdenes(); }

    @Override
    public OrderDTO obtener(Long id) { return service.obtener(id); }

    @Override
    public List<PositionDTO> misPosiciones() {
        return service.listarMisPosiciones();
    }
}
