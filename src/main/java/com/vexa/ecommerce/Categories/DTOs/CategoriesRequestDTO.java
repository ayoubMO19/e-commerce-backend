package com.vexa.ecommerce.Categories.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoriesRequestDTO {

    @NotNull
    private String name;
}
