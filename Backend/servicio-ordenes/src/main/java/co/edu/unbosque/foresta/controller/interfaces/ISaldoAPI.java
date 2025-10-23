package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/saldo")
public interface ISaldoAPI {
    @GetMapping("/obtener")
    ResponseEntity<TradingDetailDTO> obtener();
}
