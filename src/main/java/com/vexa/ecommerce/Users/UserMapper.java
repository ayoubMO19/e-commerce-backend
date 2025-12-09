package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Auth.DTOs.RegisterRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;

public class UserMapper {

    public static Users toEntity(UserRequestDTO dto) {
        Users user = new Users();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setHasWelcomeDiscount(dto.hasWelcomeDiscount());
        user.setRole(Role.USER);
        return user;
    }

    public static Users toEntity(RegisterRequestDTO dto) {
        Users user = new Users();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setHasWelcomeDiscount(true);
        user.setRole(Role.USER);
        return user;
    }
    public static UserResponseDTO toDTO(Users user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getHasWelcomeDiscount()
        );
    }
}
