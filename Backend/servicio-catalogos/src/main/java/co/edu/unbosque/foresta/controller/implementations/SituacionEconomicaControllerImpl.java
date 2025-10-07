package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.ISituacionEconomicaController;
import co.edu.unbosque.foresta.model.DTO.SituacionEconomicaDTO;
import co.edu.unbosque.foresta.service.interfaces.ISituacionEconomicaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SituacionEconomicaControllerImpl implements ISituacionEconomicaController {

    private final ISituacionEconomicaService service;

    public SituacionEconomicaControllerImpl(ISituacionEconomicaService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SituacionEconomicaDTO> crear(@Valid @RequestBody SituacionEconomicaDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SituacionEconomicaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SituacionEconomicaDTO> obtener(Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

}
