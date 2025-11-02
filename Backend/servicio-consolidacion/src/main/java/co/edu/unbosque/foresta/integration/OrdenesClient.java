package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name="consol-ordenes", url="${ordenes.base-url}",
        configuration = co.edu.unbosque.foresta.configuration.FeignAuthConfig.class)
public interface OrdenesClient {
    @GetMapping("/api/ordenes/listar")
    List<OrdenDTO> listarTodas();
}
