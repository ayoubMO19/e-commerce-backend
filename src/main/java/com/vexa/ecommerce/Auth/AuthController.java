package com.vexa.ecommerce.Auth;

import com.vexa.ecommerce.Auth.DTOs.*;
import com.vexa.ecommerce.Security.JwtService;
import com.vexa.ecommerce.Users.*;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersService usersService;

    public AuthController(JwtService jwtService,
                          UsersRepository usersRepository,
                          PasswordEncoder passwordEncoder,
                          UsersService usersService) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersService = usersService;
    }

    // REGISTRO
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        // Verificar si el email ya existe
        if (usersRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(409).build(); // 409 Conflict
        }

        // Convertir RegisterRequestDTO a entidad Users
        Users newUser = new Users(
                request.name(),
                request.surname(),
                request.email(),
                true, // hasWelcomeDiscount
                passwordEncoder.encode(request.password()),
                Role.USER
        );

        Users savedUser = usersService.saveNewUser(newUser);

        // Generar token automáticamente
        String token = jwtService.generateToken(savedUser.getEmail());

        // Convertir a UserResponseDTO
        UserResponseDTO userDTO = UserMapper.toDTO(savedUser);

        AuthResponseDTO response = new AuthResponseDTO(token, userDTO);

        return ResponseEntity.status(201).body(response);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        String token = jwtService.generateToken(request.email());
        UserResponseDTO userDTO = UserMapper.toDTO(user);
        AuthResponseDTO response = new AuthResponseDTO(token, userDTO);

        return ResponseEntity.ok(response);
    }
}