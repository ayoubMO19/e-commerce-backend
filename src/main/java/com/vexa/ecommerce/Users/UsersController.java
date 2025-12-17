package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Users.DTOs.UserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // Cambia el constructor para inyectar PasswordEncoder
    public UsersController(UsersService usersService, UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN ENDPOINTS
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UserResponseDTO> dtoList = users.stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario autenticado no encontrado"
                ));

        // Verificar que el ID y Role
        if (currentUser.getRole() != Role.ADMIN &&
                !currentUser.getUserId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    // TODO: Crear endpoint de deleteUserById

    // TODO: Crear endpoint de updateUserById


    // CLIENTS ENDPOINTS
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @Valid @RequestBody UserRequestDTO requestDTO
    ) {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users currentUser = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Users updatedUser = UserMapper.toEntity(requestDTO);
        updatedUser.setUserId(currentUser.getUserId());

        if (requestDTO.password() != null && !requestDTO.password().isEmpty()) {
            updatedUser.setPassword(passwordEncoder.encode(requestDTO.password()));
        }

        Users savedUser = usersService.updateUser(updatedUser);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    // TODO: Crear endpoint de deleteMyProfile

}