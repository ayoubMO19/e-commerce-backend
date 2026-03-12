package com.vexa.ecommerce.Auth;

import com.vexa.ecommerce.Auth.DTOs.*;
import com.vexa.ecommerce.Users.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // REGISTER
    @Operation(
            summary = "Registar un nuevo User",
            description = "Registra un nuevo User en el ecommerce",
            tags = {"Auth"}
    )
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponseDTO.class))) // Respuesta exitosa
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO registerResponseDTO = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponseDTO);
    }

    // LOGIN
    @Operation(
            summary = "Login en el sistema",
            description = "Loguearse en el sistema con usuario existente",
            tags = {"Auth"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario logueado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))) // Respuesta exitosa
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    @Hidden
    public ModelAndView verifyEmail(@RequestParam String token) {
        try {
            // Ejecutamos la lógica en el servicio
            Users user = authService.verifyEmailAndGetUser(token);

            // Si todo sale bien, mostramos la vista de éxito
            ModelAndView mav = new ModelAndView("email/verification-success");
            mav.addObject("userName", user.getName());
            mav.addObject("frontendUrl", frontendUrl); // Variable @Value del controller
            return mav;

        } catch (Exception e) {
            // Si hay cualquier error (expirado, usado, no existe), mostramos vista de error
            ModelAndView mav = new ModelAndView("email/verification-error");
            mav.addObject("errorMessage", e.getMessage());
            return mav;
        }
    }

    // FORGOT PASSWORD
    @Operation(
            summary = "Recuperación de password",
            description = "Recuperar password olvidada utilizando el email",
            tags = {"Auth"}
    )
    @ApiResponse(responseCode = "200", description = "Email de recuperación de password enviado exitosamente") // Respuesta exitosa
    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password-form")
    @Hidden
    public ModelAndView resetPasswordForm(@RequestParam String token) {
        // ModelAndView le dice a Spring: "Busca una plantilla, no devuelvas texto"
        ModelAndView mav = new ModelAndView("email/reset-password-form"); // Asegúrate de poner la ruta correcta si está en una carpeta
        mav.addObject("token", token);
        mav.addObject("frontendUrl", frontendUrl);
        return mav;
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    @Hidden
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}