package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.configuration.FeignAuthConfig;
import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "inversionistas-auth",
        url = "${inversionistas.base-url}",
        configuration = {FeignAuthConfig.class}
)
public interface InversionistasAuthClient {
    @GetMapping("/api/inversionistas/mi/alpaca")
    AlpacaAccountDTO miAlpaca();
}
