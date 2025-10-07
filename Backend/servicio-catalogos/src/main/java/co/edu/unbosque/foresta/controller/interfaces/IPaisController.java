package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.PaisDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/catalogos/paises")
public interface IPaisController {

    @PostMapping("/crear")
    ResponseEntity<PaisDTO> crear(@RequestBody PaisDTO dto);

    @GetMapping("/listar")
    ResponseEntity<List<PaisDTO>> listar();

    @GetMapping("/obtener/{id}")
    ResponseEntity<PaisDTO> obtener(@PathVariable Long id);

}
