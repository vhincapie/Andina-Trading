package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.service.interfaces.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarResetPassword(String destinatario, String token) {
        String enlace = String.format("%s/reset-password?token=%s", frontendBaseUrl, token);

        String subject = "Recuperación de contraseña - Foresta";
        String text = """
                Hola,
                
                Recibimos una solicitud para restablecer tu contraseña.
                Para continuar, haz clic en el siguiente enlace (válido por 30 minutos):
                
                %s
                
                Si no fuiste tú, ignora este mensaje.
                
                Saludos,
                Equipo Foresta
                """.formatted(enlace);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destinatario);
        msg.setSubject(subject);
        msg.setText(text);

        mailSender.send(msg);
    }
}

