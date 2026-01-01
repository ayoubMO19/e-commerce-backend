package com.vexa.ecommerce.Auth;

import com.vexa.ecommerce.Auth.DTOs.*;
import com.vexa.ecommerce.Security.JwtService;
import com.vexa.ecommerce.Users.*;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersService usersService;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final TemplateEngine templateEngine;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthService(JwtService jwtService,
                       UsersRepository usersRepository,
                       PasswordEncoder passwordEncoder,
                       UsersService usersService,
                       EmailService emailService,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       TemplateEngine templateEngine, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersService = usersService;
        this.emailService = emailService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.templateEngine = templateEngine;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        // Verificar si el email ya existe
        if (usersRepository.findByEmail(request.email()).isPresent()) {
            // Crear respuesta de error
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El email ya está registrado"
            );
        }

        // Crear usuario NO verificado
        Users newUser = new Users(
                request.name(),
                request.surname(),
                request.email(),
                true, // hasWelcomeDiscount
                passwordEncoder.encode(request.password()),
                Role.USER
        );
        newUser.setEnabled(false);

        Users savedUser = usersService.saveNewUser(newUser);

        // Generar y guardar token
        EmailVerificationToken verificationToken = new EmailVerificationToken(savedUser);
        emailVerificationTokenRepository.save(verificationToken);

        // Intentar enviar email
        boolean emailSent = true;
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());
        } catch (MessagingException e) {
            emailSent = false;
            log.warn("Error al enviar email de verificación: {}", e.getMessage());
            // TODO: Una opcion de estas: 1. Reintentar, 2. Notificar admin, 3. Guardar en cola
        }

        // Respuesta informativa
        return new RegisterResponseDTO(
                "Usuario registrado. " +
                        (emailSent ? "Revisa tu email para verificar la cuenta." :
                                "No se pudo enviar el email de verificación. Contacta con soporte."),
                UserMapper.toDTO(savedUser),
                emailSent
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Por favor verifica tu email antes de iniciar sesión"
            );
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        String token = jwtService.generateToken(request.email());
        UserResponseDTO userDTO = UserMapper.toDTO(user);

        return new AuthResponseDTO(token, userDTO);
    }

    public String verifyEmail(String token) {
        try {
            // Buscar token en BD
            EmailVerificationToken verificationToken = emailVerificationTokenRepository
                    .findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Token no encontrado"));

            // Verificar que el token sea válido
            if (verificationToken.isUsed()) {
                return "redirect:/error?message=Este+token+ya+fue+utilizado";
            }

            if (verificationToken.isExpired()) {
                return "redirect:/error?message=El+token+ha+expirado.+Solicita+uno+nuevo";
            }

            // Obtener el usuario
            Users user = verificationToken.getUser();

            // Activar la cuenta
            user.setEnabled(true);
            usersRepository.save(user);

            // Marcar token como usado
            verificationToken.setUsed(true);
            emailVerificationTokenRepository.save(verificationToken);

            // Enviar email de bienvenida
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            } catch (MessagingException e) {
                log.error("Error al enviar email de bienvenida: {}", e.getMessage());
            }

            // Preparar y renderizar template de éxito
            Context context = new Context();
            context.setVariable("userName", user.getName());
            context.setVariable("loginUrl", "http://localhost:8082/api/auth/login");

            return templateEngine.process("email/verification-success", context);

        } catch (RuntimeException e) {
            // Si hay error, renderizar template de error
            Context errorContext = new Context();
            errorContext.setVariable("errorMessage", e.getMessage());
            return templateEngine.process("email/verification-error", errorContext);
        }
    }

    public void forgotPassword(ForgotPasswordRequestDTO request) {
        // Buscar usuario por email
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un usuario con el email introducido"
                ));

        // Verificar que el usuario esté habilitado
        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Primero debes verificar tu email"
            );
        }

        // Buscar token existente
        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository
                .findByUserEmail(request.email());

        PasswordResetToken resetToken;

        if (existingToken.isPresent()) {
            // Si existe, actualizarlo (nuevo token, nueva fecha)
            resetToken = existingToken.get();
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            resetToken.setUsed(false);
        } else {
            // Si no existe, crear nuevo
            resetToken = new PasswordResetToken(user);
        }

        passwordResetTokenRepository.save(resetToken);

        // Enviar email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
            log.info("Email de recuperación enviado");
        } catch (MessagingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al enviar email. Intenta de nuevo."
            );
        }
    }

    public String resetPasswordForm(String token) {
        // Verificar token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (!resetToken.isValid()) {
            throw new RuntimeException("Token expirado o ya utilizado");
        }

        // Preparar template
        Context context = new Context();
        context.setVariable("token", token);

        return templateEngine.process("email/reset-password-form", context);
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        // Verificar token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token inválido"
                ));

        if (!resetToken.isValid()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expirado o ya utilizado"
            );
        }

        // Obtener usuario
        Users user = resetToken.getUser();

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        usersRepository.save(user);

        // Invalidar token
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Enviar email de confirmación
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Contraseña actualizada",
                    "Tu contraseña ha sido cambiada exitosamente."
            );
        } catch (MessagingException e) {
            // Solo log, no fallar
            log.error("Error al enviar email de confirmación: {}", e.getMessage());
        }
    }
}
