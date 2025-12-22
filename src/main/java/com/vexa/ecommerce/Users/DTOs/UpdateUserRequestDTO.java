package com.vexa.ecommerce.Users.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @Size(min = 2, max = 50)
        String name,

        @Size(min = 2, max = 50)
        String surname,

        @Email
        String email,

        Boolean hasWelcomeDiscount
) {}