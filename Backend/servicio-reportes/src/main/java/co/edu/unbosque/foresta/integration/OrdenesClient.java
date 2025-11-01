package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name="ordenes-service", url="${ordenes.base-url}")
public interface OrdenesClient {
    @GetMapping("/api/ordenes/listar")
    List<OrdenDTO> listarTodas();
}
