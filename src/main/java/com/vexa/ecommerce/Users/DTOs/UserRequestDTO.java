package com.vexa.ecommerce.Users.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String name,

        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String surname,

        @Email(message = "El formato del email no es válido")
        String email,

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        Boolean hasWelcomeDiscount,

        Integer userId // Solo se usa en endpoints de admin
) {}