package com.vexa.ecommerce.Auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.vexa.ecommerce.Utils.SecurityUtils.hideSecureEmail;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${app.mail.from}")
    private String fromEmail;
    @Value("${app.url}")
    private String appUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
        String verificationUrl = appUrl + "/api/auth/verify?token=" + token;

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

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  // true = HTML

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException {
        String resetUrl = appUrl + "/api/auth/reset-password-form?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);

        String htmlContent = templateEngine.process("email/reset-password", context);
        sendEmail(toEmail, "Restablece tu contraseña - Vexa E-commerce", htmlContent);

        log.info("Email de recuperación enviado a {}", hideSecureEmail(toEmail));
    }
}