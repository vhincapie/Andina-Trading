package co.edu.unbosque.foresta.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "catalogos-service", url = "${catalogos.base-url}")
public interface CatalogosClient {

    @GetMapping("/api/catalogos/paises/obtener/{id}")
    Object obtenerPais(@PathVariable("id") Long id);

    @GetMapping("/api/catalogos/ciudades/obtener/{id}")
    Object obtenerCiudad(@PathVariable("id") Long id);
}
