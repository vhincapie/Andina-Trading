package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IPaisController;
import co.edu.unbosque.foresta.model.DTO.PaisDTO;
import co.edu.unbosque.foresta.service.interfaces.IPaisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaisControllerImpl implements IPaisController {

    private final IPaisService service;

    public PaisControllerImpl(IPaisService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaisDTO> crear(@Valid @RequestBody PaisDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @Override
    public ResponseEntity<List<PaisDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Override
    public ResponseEntity<PaisDTO> obtener(Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

}
