package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name="inversionistas-service", url="${inversionistas.base-url}")
public interface InversionistasClient {
    @GetMapping("/api/inversionistas/listar")
    List<InversionistaDTO> listar();
}
