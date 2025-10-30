package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.OrdenFilledEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "servicio-notificaciones",
        url = "http://servicio-notificaciones:8089",
        configuration = co.edu.unbosque.foresta.configuration.FeignInternalConfig.class
)
public interface NotificacionesClient {

    @PostMapping("/api/notificaciones/orden-filled")
    void notificarOrdenFilled(OrdenFilledEmailRequest body);
}