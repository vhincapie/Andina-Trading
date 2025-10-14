package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IInversionistaController;
import co.edu.unbosque.foresta.model.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaUpdateRequestDTO;
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

    public InversionistaControllerImpl(IInversionistaService service) {
        this.service = service;
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

}
