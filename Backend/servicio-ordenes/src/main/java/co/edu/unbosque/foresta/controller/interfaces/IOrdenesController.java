package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.OrderCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.OrderDTO;
import co.edu.unbosque.foresta.model.DTO.PositionDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/ordenes")
public interface IOrdenesController {

    @PostMapping("/crear")
    @PreAuthorize("hasRole('INVERSIONISTA')")
    OrderDTO crear(@RequestBody OrderCreateRequestDTO req);

    @GetMapping("/mis-ordenes")
    @PreAuthorize("hasRole('INVERSIONISTA')")
    List<OrderDTO> misOrdenes();

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('INVERSIONISTA')")
    OrderDTO obtener(@PathVariable Long id);

    @GetMapping("/mis-posiciones")
    @PreAuthorize("hasRole('INVERSIONISTA')")
    List<PositionDTO> misPosiciones();
}
