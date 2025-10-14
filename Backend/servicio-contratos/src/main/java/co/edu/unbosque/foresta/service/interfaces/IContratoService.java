package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;

import java.util.List;

public interface IContratoService {
    ContratoDTO registrar(ContratoRegistroRequestDTO req);
    ContratoDTO obtenerMiContratoActivo();
    ContratoDTO cancelarMiContratoActivo();
    ContratoDTO listarPorInversionistaParaComisionista(Long inversionistaId);
}
