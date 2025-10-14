package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaUpdateRequestDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RequestMapping("/api/inversionistas")
public interface IInversionistaController {

    @PostMapping("/registrar")
    InversionistaDTO registrar(@RequestBody @Valid InversionistaRegistroRequestDTO req);

    @GetMapping("/perfil")
    InversionistaDTO miPerfil(@AuthenticationPrincipal String username);

    @PutMapping("/actualizar")
    InversionistaDTO actualizar(@AuthenticationPrincipal String username,
                                @RequestBody @Valid InversionistaUpdateRequestDTO req);

    @GetMapping("/{id}")
    InversionistaDTO obtenerPorId(@PathVariable Long id);
}
