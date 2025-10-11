package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IComisionistaController;
import co.edu.unbosque.foresta.model.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.model.DTO.ComisionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.service.interfaces.IComisionistaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
public class ComisionistaControllerImpl implements IComisionistaController {

    private final IComisionistaService service;

    public ComisionistaControllerImpl(IComisionistaService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ComisionistaDTO registrar(@Valid ComisionistaRegistroRequestDTO req,
                                     HttpServletRequest request) {
        return service.registrar(req, request.getRemoteAddr());
    }

    @Override
    @PreAuthorize("hasRole('COMISIONISTA')")
    public ComisionistaDTO perfil(Principal principal) {
       if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
       return service.perfil(principal.getName());
    }

}
