package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.OrdenFilledEmailRequest;
import co.edu.unbosque.foresta.service.interfaces.IEmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarOrdenFilled(OrdenFilledEmailRequest r) {
        String ladoEs = (r.getSide() == null) ? "ORDEN"
                : ("BUY".equalsIgnoreCase(r.getSide()) ? "COMPRA"
                : ("SELL".equalsIgnoreCase(r.getSide()) ? "VENTA"
                : r.getSide().toUpperCase()));

        String ladoEsBody = Character.toUpperCase(ladoEs.charAt(0)) + ladoEs.substring(1).toLowerCase();

        String symbol = (r.getSymbol() != null && !r.getSymbol().isBlank())
                ? r.getSymbol().toUpperCase()
                : "—";

        String asunto = "Tu orden de " + ladoEs + " de " + symbol + " fue EJECUTADA";

        String cuerpo = """
        Hola %s,

        ¡Buenas noticias! Tu orden ha sido EJECUTADA:

        • Símbolo: %s
        • Tipo: %s
        • Cantidad: %s
        • Precio unitario: %s %s
        • Monto neto: %s %s
        %s

        Gracias por operar con Andina Trading.
        """.formatted(
                safe(r.getInversionistaNombre()),
                symbol,
                ladoEsBody,
                safe(r.getQty()),
                r.getUnitPrice() != null ? r.getUnitPrice().toPlainString() : "-",
                safe(r.getMoneda()),
                r.getNetAmount() != null ? r.getNetAmount().toPlainString() : "-",
                safe(r.getMoneda()),
                r.getFilledAt() != null ? ("• Ejecutada en: " + r.getFilledAt()) : ""
        );

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(r.getInversionistaCorreo());
        msg.setSubject(asunto);
        msg.setText(cuerpo);

        mailSender.send(msg);
    }

    private static String safe(String s){ return s==null? "": s; }
}
