package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="comisionistas-service", url="${comisionistas.base-url}")
public interface ComisionistasClient {
    @GetMapping("/api/comisionistas/perfil")
    ComisionistaDTO miPerfil();
}
