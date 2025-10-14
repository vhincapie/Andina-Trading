package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.model.DTO.ComisionistaRegistroRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/comisionistas")
public interface IComisionistaController {

    @PostMapping("/registrar")
    ComisionistaDTO registrar(@RequestBody @Valid ComisionistaRegistroRequestDTO req,
                              HttpServletRequest request);

    @GetMapping("/perfil")
    ComisionistaDTO perfil(Principal principal);

    @GetMapping("/listar")
    List<ComisionistaDTO> listarTodos();

    @GetMapping("/{id}")
    ComisionistaDTO obtenerPorId(@PathVariable Long id);
}
