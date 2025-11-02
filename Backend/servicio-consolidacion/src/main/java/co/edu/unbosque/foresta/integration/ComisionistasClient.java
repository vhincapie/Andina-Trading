package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name="consol-comisionistas", url="${comisionistas.base-url}",
        configuration = co.edu.unbosque.foresta.configuration.FeignAuthConfig.class)
public interface ComisionistasClient {
    @GetMapping("/api/comisionistas/listar")
    List<ComisionistaDTO> listar();
}
