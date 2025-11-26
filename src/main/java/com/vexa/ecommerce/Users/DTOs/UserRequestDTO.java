package com.vexa.ecommerce.Users.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Email
    private String email;

    @NotNull
    private String password;

    private Boolean hasWelcomeDiscount;
}
