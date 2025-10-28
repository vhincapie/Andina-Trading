package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IInversionistaController;
import co.edu.unbosque.foresta.model.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaUpdateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.service.interfaces.IAlpacaAccountQueryService;
import co.edu.unbosque.foresta.service.interfaces.IInversionistaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

@RestController
public class InversionistaControllerImpl implements IInversionistaController {

    private final IInversionistaService service;
    private final IAlpacaAccountQueryService alpacaQuery;

    public InversionistaControllerImpl(IInversionistaService service, IAlpacaAccountQueryService alpacaQuery) {
        this.service = service;
        this.alpacaQuery = alpacaQuery;
    }

    @Override
    public InversionistaDTO registrar(@RequestBody @Valid InversionistaRegistroRequestDTO req) {
        return service.registrar(req);
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public InversionistaDTO miPerfil(@AuthenticationPrincipal String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return service.obtenerMiPerfil(username);
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public InversionistaDTO actualizar(@AuthenticationPrincipal String username,
                                       @RequestBody @Valid InversionistaUpdateRequestDTO req) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return service.actualizar(username, req);
    }

    @Override
    @PreAuthorize("hasAnyRole('COMISIONISTA','INVERSIONISTA','ADMIN')")
    public InversionistaDTO obtenerPorId(Long id) {
        return service.obtenerPorId(id);
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public AlpacaAccountDTO miAlpaca(@AuthenticationPrincipal String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return service.miAlpaca(username);
    }

    @Override
    @PreAuthorize("hasAnyRole('COMISIONISTA','ADMIN','INVERSIONISTA')")
    public AlpacaAccountDTO alpacaPorId(Long id) {

        InversionistaDTO inv = service.obtenerPorId(id);
        if (inv == null) throw new RuntimeException("Inversionista no encontrado");
        return alpacaQuery.getByInversionistaId(id);
    }

}
