package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.PaisDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "catalogos", url = "${catalogos.base-url}")
public interface CatalogosClient {

    @GetMapping("/api/catalogos/paises/listar")
    List<PaisDTO> listarPaises();

    @GetMapping("/api/catalogos/paises/obtener/{id}")
    PaisDTO obtener(@PathVariable("id") Long id);
}
