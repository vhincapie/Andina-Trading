package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="inversionistas-service", url="${inversionistas.base-url}")
public interface InversionistasClient {

    @GetMapping("/api/inversionistas/mi/alpaca")
    AlpacaAccountDTO miAlpaca();

    @GetMapping("/api/inversionistas/{id}")
    InversionistaDTO obtenerPorId(@PathVariable("id") Long id);

    @GetMapping("/api/inversionistas/{id}/alpaca")
    AlpacaAccountDTO alpacaPorInversionistaId(@PathVariable("id") Long id);
}
