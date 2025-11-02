package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name="consol-inversionistas", url="${inversionistas.base-url}",
        configuration = co.edu.unbosque.foresta.configuration.FeignAuthConfig.class)
public interface InversionistasClient {
    @GetMapping("/api/inversionistas/listar")
    List<InversionistaDTO> listar();
}
