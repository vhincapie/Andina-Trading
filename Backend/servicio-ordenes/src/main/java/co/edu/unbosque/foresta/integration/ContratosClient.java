package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.ContratoActivoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="contratos-service", url="${contratos.base-url}")
public interface ContratosClient {
    @GetMapping("/api/contratos/mi-contrato")
    ContratoActivoDTO miContratoActivo();
}
