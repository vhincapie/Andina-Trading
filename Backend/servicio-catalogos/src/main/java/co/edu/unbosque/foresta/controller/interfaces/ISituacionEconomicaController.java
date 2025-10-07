package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.SituacionEconomicaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/catalogos/situaciones-economicas")
public interface ISituacionEconomicaController {

    @PostMapping("/crear")
    ResponseEntity<SituacionEconomicaDTO> crear(@RequestBody SituacionEconomicaDTO dto);

    @GetMapping("/listar")
    ResponseEntity<List<SituacionEconomicaDTO>> listar();

    @GetMapping("/obtener/{id}")
    ResponseEntity<SituacionEconomicaDTO> obtener(@PathVariable Long id);

}
