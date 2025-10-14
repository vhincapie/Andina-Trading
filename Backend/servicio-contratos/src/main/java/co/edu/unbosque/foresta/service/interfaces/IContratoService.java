package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IContratoService {
    ContratoDTO registrar(ContratoRegistroRequestDTO req);
    ContratoDTO obtenerMiContratoActivo();
    ContratoDTO cancelarMiContratoActivo();
    List<ContratoDTO> listarMisContratosComisionistaAutenticado();
}
