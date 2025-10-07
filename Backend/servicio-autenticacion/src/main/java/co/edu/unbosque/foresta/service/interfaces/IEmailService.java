package co.edu.unbosque.foresta.service.interfaces;

public interface IEmailService {
    void enviarResetPassword(String destinatario, String token);
}
