package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.model.DTO.CiudadLiteDTO;
import co.edu.unbosque.foresta.model.DTO.PaisLiteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalogos-service", url = "${catalogos.base-url}")
public interface CatalogosClient {
    @GetMapping("/api/catalogos/paises/obtener/{id}")
    PaisLiteDTO obtenerPais(@PathVariable("id") Long id);

    @GetMapping("/api/catalogos/ciudades/obtener/{id}")
    CiudadLiteDTO obtenerCiudad(@PathVariable("id") Long id);
}

