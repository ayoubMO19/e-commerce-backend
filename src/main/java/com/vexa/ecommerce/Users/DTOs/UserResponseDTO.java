package com.vexa.ecommerce.Users.DTOs;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer userId;
    private String name;
    private String surname;
    private String email;
    private Boolean hasWelcomeDiscount;
}
