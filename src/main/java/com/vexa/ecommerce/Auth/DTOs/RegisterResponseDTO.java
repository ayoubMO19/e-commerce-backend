package com.vexa.ecommerce.Auth.DTOs;

import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;

public record RegisterResponseDTO(
        String message,
        UserResponseDTO user,
        boolean emailSent
) {}