package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/contratos")
public interface IContratoController {

    @PostMapping("/registrar")
    ContratoDTO registrar(@RequestBody ContratoRegistroRequestDTO req);

    @GetMapping("/mi-contrato")
    ContratoDTO obtenerMiContratoActivo();

    @PutMapping("/cancelar")
    ContratoDTO cancelarMiContratoActivo();

    @GetMapping("/mis-contratos")
    List<ContratoDTO> listarMisContratos();

}
