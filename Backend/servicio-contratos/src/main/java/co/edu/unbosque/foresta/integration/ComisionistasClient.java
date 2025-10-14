package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.ComisionistaPerfilDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="comisionistas-service", url="${comisionistas.base-url}")
public interface ComisionistasClient {
    @GetMapping("/api/comisionistas/{id}")
    ComisionistaPerfilDTO obtenerPorId(@PathVariable("id") Long id);

    @GetMapping("/api/comisionistas/perfil")
    ComisionistaPerfilDTO miPerfil();
}
