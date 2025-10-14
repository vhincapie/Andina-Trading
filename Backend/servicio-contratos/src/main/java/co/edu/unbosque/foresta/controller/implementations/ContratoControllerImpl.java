package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IContratoController;
import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.service.interfaces.IContratoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ContratoControllerImpl implements IContratoController {

    private final IContratoService service;
    public ContratoControllerImpl(IContratoService service){ this.service = service; }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ContratoDTO registrar(ContratoRegistroRequestDTO req) {
        try {
            return service.registrar(req);
        } catch (ResponseStatusException ex) {
            throw ex;
        }
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ContratoDTO obtenerMiContratoActivo() {
        return service.obtenerMiContratoActivo();
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ContratoDTO cancelarMiContratoActivo() {
        return service.cancelarMiContratoActivo();
    }

    @Override
    @PreAuthorize("hasRole('COMISIONISTA')")
    public ContratoDTO listarPorInversionistaParaComisionista(Long inversionistaId) {
        return service.listarPorInversionistaParaComisionista(inversionistaId);
    }

}
