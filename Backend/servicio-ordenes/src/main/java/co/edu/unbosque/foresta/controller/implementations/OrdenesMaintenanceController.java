package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.service.implementations.OrderSyncService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenesMaintenanceController {

    private final OrderSyncService syncService;

    public OrdenesMaintenanceController(OrderSyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sincronizar-estados")
    @PreAuthorize("hasAnyRole('INVERSIONISTA','COMISIONISTA','ADMIN')")
    public Map<String,Object> syncNow() {
        syncService.sincronizarEstadosOrdenes();
        return Map.of("ok", true);
    }
}
