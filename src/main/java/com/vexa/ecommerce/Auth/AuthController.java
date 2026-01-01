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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

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

    // EMAIL
    @GetMapping("/verify")
    @Hidden
    public String verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
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

    // RESET PASSWORD FORM (página HTML)
    @GetMapping("/reset-password-form")
    @Hidden
    public String resetPasswordForm(@RequestParam String token) {
        return authService.resetPasswordForm(token);
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    @Hidden
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}