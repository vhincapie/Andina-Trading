package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "inversionistas-service", url = "${inversionistas.base-url}", configuration = co.edu.unbosque.foresta.configuration.FeignAuthConfig.class)
public interface InversionistaClient {
    @GetMapping("/api/inversionistas/mi/alpaca")
    MiAlpacaDTO miAlpaca();
}
