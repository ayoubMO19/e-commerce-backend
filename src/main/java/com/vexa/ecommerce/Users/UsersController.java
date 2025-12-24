package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Security.UserDetailsImpl;
import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
public class UsersController {

    private final UsersService usersService;

    // Cambia el constructor para inyectar PasswordEncoder
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // ADMIN ENDPOINTS
    @Tag(name = "Admin Endpoints", description = "Endpoints para gestionar todo el sistema")
    @Operation(
            summary = "(ADMIN) - Obtener todos los usuarios del sistema",
            description = "Retorna todos los usuarios del sistema",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UserResponseDTO> dtoList = users.stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Operation(
            summary = "(ADMIN) - Obtener usuario por id",
            description = "Retorna un usuario indicando su id",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario obtenido existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @Operation(
            summary = "(ADMIN) - Eliminar user por id",
            description = "Eliminar un usuario indicando su id",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario eliminado existosamente") // Respuesta exitosa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        usersService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "(ADMIN) - Actualizar usuario por id",
            description = "Actualizar un usuario indicando su id y sus nuevos datos",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario actualizado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    public ResponseEntity<UserResponseDTO> updateUserById(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequestDTO dto
    ) {
        Users updateUser = usersService.updateUser(userId, dto);
        return ResponseEntity.ok(UserMapper.toDTO(updateUser));
    }


    // CLIENTS ENDPOINTS
    @Tag(name = "Users", description = "Endpoints para gestionar perfil de usaurio")
    @Operation(
            summary = "Obtener perfil",
            description = "Obtener el perfil del user logueado",
            tags = {"Users"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario obtenido existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Users user = usersService.getUserById(userDetails.getUserId());
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @Operation(
            summary = "Actualizar perfil",
            description = "Actualizar el perfil del user logueado",
            tags = {"Users"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario actualizado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateUserRequestDTO dto
    ) {
        Users savedUser = usersService.updateUser(userDetails.getUserId(), dto);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    @Operation(
            summary = "Eliminar perfil",
            description = "Eliminar el perfil del user logueado",
            tags = {"Users"}
    )
    @ApiResponse(responseCode = "200", description = "Usuario Eliminado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))) // Respuesta exitosa
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile (
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        usersService.deleteUserById(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}