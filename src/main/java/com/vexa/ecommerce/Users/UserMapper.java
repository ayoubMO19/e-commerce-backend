package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Users.DTOs.UserRequestDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;

public class UserMapper {

    public static Users toEntity(UserRequestDTO dto) {
        Users user = new Users();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setHasWelcomeDiscount(dto.getHasWelcomeDiscount());
        return user;
    }

    public static UserResponseDTO toDTO(Users user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setHasWelcomeDiscount(user.getHasWelcomeDiscount());
        return dto;
    }
}
