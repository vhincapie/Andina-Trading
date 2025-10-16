package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/cuentas")
public interface ISaldoController {
    @GetMapping("/saldo")
    ResponseEntity<TradingDetailDTO> obtenerSaldo();
}
