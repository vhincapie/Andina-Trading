package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IAccountACHController;
import co.edu.unbosque.foresta.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.foresta.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.foresta.service.interfaces.IAccountACHService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountACHController implements IAccountACHController {

    private final IAccountACHService<AccountACHRelationShipDTO> service;

    public AccountACHController(IAccountACHService<AccountACHRelationShipDTO> service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ResponseEntity<ResponseAccountACHDTO> create(AccountACHRelationShipDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ResponseEntity<List<AccountACHRelationShipDTO>> getAllForAuthenticatedUser() {
        return ResponseEntity.ok(service.getAllForAuthenticatedUser());
    }
}
