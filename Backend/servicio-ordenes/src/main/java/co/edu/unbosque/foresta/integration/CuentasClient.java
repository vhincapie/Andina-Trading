package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cuentas-service", url = "${cuentas.base-url}")
public interface CuentasClient {

    @GetMapping("/api/cuentas/saldo")
    ResponseEntity<TradingDetailDTO> obtenerSaldo();
}
