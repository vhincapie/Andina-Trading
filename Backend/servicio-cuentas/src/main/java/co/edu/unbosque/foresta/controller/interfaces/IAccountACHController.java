package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.foresta.model.DTO.ResponseAccountACHDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/cuentas/ach")
public interface IAccountACHController {
    @PostMapping("/crear")
    ResponseEntity<ResponseAccountACHDTO> create(@RequestBody AccountACHRelationShipDTO dto);
    @GetMapping("/obtener")
    ResponseEntity<List<AccountACHRelationShipDTO>> getAllForAuthenticatedUser();
}
