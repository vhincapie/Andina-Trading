package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.OrdenFilledEmailRequest;

public interface IEmailService {
    void enviarOrdenFilled(OrdenFilledEmailRequest r);
}
