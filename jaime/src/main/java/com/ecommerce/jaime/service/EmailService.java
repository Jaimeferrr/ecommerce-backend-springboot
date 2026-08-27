package com.ecommerce.jaime.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvoiceEmail(String toEmail, Long orderId, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Confirmación de Pedido #" + orderId + " - E-Commerce Jaime");
            helper.setText(
                    "<h3>¡Gracias por tu compra!</h3>" +
                            "<p>Adjunto a este correo encontrarás la factura oficial de tu pedido <strong>#" + orderId + "</strong>.</p>" +
                            "<br><p>Saludos,<br><strong>Equipo de E-Commerce Jaime</strong></p>",
                    true // true para enviar HTML
            );

            helper.addAttachment("Factura_Pedido_" + orderId + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo con la factura", e);
        }
    }
}