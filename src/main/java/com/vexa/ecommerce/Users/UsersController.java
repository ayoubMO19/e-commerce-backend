package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Security.UserDetailsImpl;
import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    // Cambia el constructor para inyectar PasswordEncoder
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
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
        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        usersService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserById(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequestDTO dto
    ) {
        Users updateUser = usersService.updateUser(userId, dto);
        return ResponseEntity.ok(UserMapper.toDTO(updateUser));
    }


    // CLIENTS ENDPOINTS
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Users user = usersService.getUserById(userDetails.getUserId());
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateUserRequestDTO dto
    ) {
        Users savedUser = usersService.updateUser(userDetails.getUserId(), dto);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile (
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        usersService.deleteUserById(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}