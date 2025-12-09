package com.vexa.ecommerce.Users.DTOs;

public record UserResponseDTO(
        Integer userId,
        String name,
        String surname,
        String email,
        Boolean hasWelcomeDiscount
) {}