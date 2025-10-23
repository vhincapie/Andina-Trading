package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.CuentasClient;
import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import co.edu.unbosque.foresta.service.interfaces.ISaldoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SaldoServiceImpl implements ISaldoService {

    private final CuentasClient cuentasClient;

    public SaldoServiceImpl(CuentasClient cuentasClient) {
        this.cuentasClient = cuentasClient;
    }

    @Override
    public TradingDetailDTO obtenerSaldoUsuarioActual() {
        ResponseEntity<TradingDetailDTO> res = cuentasClient.obtenerSaldo();
        if (res == null || res.getBody() == null) {
            throw new RuntimeException("No fue posible obtener el saldo desde cuentas.");
        }
        return res.getBody();
    }
}
