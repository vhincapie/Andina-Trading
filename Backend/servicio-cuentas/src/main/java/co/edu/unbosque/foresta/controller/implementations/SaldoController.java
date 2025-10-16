package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.ISaldoController;
import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import co.edu.unbosque.foresta.service.interfaces.ISaldoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaldoController implements ISaldoController {

    private final ISaldoService saldoService;

    public SaldoController(ISaldoService saldoService) {
        this.saldoService = saldoService;
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ResponseEntity<TradingDetailDTO> obtenerSaldo() {
        TradingDetailDTO body = saldoService.obtenerSaldoUsuarioActual();
        String notice = saldoService.calcularAvisoParaUsuarioActual();
        HttpHeaders headers = new HttpHeaders();
        if (notice != null && !notice.isBlank()) headers.add("X-Notice", notice);
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
