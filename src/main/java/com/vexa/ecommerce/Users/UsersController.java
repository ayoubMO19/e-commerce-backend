package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Users.DTOs.UserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UserResponseDTO> dtoList = users.stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        Users newUser = UserMapper.toEntity(requestDTO);
        Users savedUser = usersService.saveNewUser(newUser);

        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequestDTO requestDTO
    ) {
        Users updatedUser = UserMapper.toEntity(requestDTO);
        updatedUser.setUserId(id);

        Users savedUser = usersService.updateUser(updatedUser);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        usersService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
