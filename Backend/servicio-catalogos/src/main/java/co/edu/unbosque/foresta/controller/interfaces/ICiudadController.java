package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.CiudadDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/catalogos/ciudades")
public interface ICiudadController {

    @PostMapping("/crear")
    ResponseEntity<CiudadDTO> crear(@RequestBody CiudadDTO dto);

    @GetMapping("/listar")
    ResponseEntity<List<CiudadDTO>> listar();

    @GetMapping("/obtener/{id}")
    ResponseEntity<CiudadDTO> obtener(@PathVariable Long id);

}
