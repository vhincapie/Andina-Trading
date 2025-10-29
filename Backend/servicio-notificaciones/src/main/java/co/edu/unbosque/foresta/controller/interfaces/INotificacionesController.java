package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.OrdenFilledEmailRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/notificaciones")
public interface INotificacionesController {

    @PostMapping("/orden-filled")
    ResponseEntity<Void> ordenFilled(@RequestBody @Valid OrdenFilledEmailRequest req);

}
