package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;

public class UserMapper {

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
