package com.vexa.ecommerce.Categories.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriesRequestDTO {

    @NotBlank
    private String name;
}
