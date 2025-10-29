package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.INotificacionesController;
import co.edu.unbosque.foresta.model.DTO.OrdenFilledEmailRequest;
import co.edu.unbosque.foresta.service.interfaces.IEmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificacionesControllerImpl implements INotificacionesController {

    private final IEmailService emailService;

    public NotificacionesControllerImpl(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public ResponseEntity<Void> ordenFilled(@Valid OrdenFilledEmailRequest req) {
        emailService.enviarOrdenFilled(req);
        return ResponseEntity.accepted().build();
    }

}
