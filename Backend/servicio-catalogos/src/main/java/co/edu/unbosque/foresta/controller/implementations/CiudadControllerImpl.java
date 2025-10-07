package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.ICiudadController;
import co.edu.unbosque.foresta.model.DTO.CiudadDTO;
import co.edu.unbosque.foresta.service.interfaces.ICiudadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CiudadControllerImpl implements ICiudadController {

    private final ICiudadService service;

    public CiudadControllerImpl(ICiudadService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CiudadDTO> crear(@Valid @RequestBody CiudadDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @Override
    public ResponseEntity<List<CiudadDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Override
    public ResponseEntity<CiudadDTO> obtener(Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }


}
