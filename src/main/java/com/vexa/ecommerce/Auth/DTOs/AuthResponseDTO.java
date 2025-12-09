package com.vexa.ecommerce.Auth.DTOs;

import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;

public record AuthResponseDTO(
        String token,
        UserResponseDTO user
) {}