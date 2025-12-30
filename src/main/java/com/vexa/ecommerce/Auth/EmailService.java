package com.vexa.ecommerce.Auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
        String verificationUrl = "http://localhost:8082/api/auth/verify?token=" + token;

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);

        String htmlContent = templateEngine.process("email/verification", context);
        sendEmail(toEmail, "Verifica tu cuenta - Vexa E-commerce", htmlContent);

        log.info("Email de verificación enviado a {}", hideSecureEmail(toEmail));
    }

    public void sendWelcomeEmail(String toEmail, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);

        String htmlContent = templateEngine.process("email/welcome", context);
        sendEmail(toEmail, "¡Bienvenido a Vexa E-commerce!", htmlContent);

        log.info("Email de bienvenida enviado a {}", hideSecureEmail(toEmail));
    }

    public void sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  // true = HTML

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException {
        String resetUrl = "http://localhost:8082/api/auth/reset-password-form?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);

        String htmlContent = templateEngine.process("email/reset-password", context);
        sendEmail(toEmail, "Restablece tu contraseña - Vexa E-commerce", htmlContent);

        log.info("Email de recuperación enviado a {}", hideSecureEmail(toEmail));
    }

    public static String hideSecureEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        int arrobaIndex = email.indexOf('@');
        if (arrobaIndex == -1) {
            return email; // No es un email válido
        }

        String usuario = email.substring(0, arrobaIndex);
        String dominio = email.substring(arrobaIndex + 1);

        // Mostrar solo los primeros 3 caracteres del usuario y los primeros 3 del dominio
        String partialUser = usuario.substring(0, Math.min(usuario.length(), 3)) + "...";
        String partialDomain = dominio.substring(0, Math.min(dominio.length(), 3)) + "...";

        return partialUser + "@" + partialDomain; // Salida: usu...@dom...
    }
}