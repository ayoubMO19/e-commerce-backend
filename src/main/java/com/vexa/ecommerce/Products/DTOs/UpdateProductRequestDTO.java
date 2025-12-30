package com.vexa.ecommerce.Products.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record UpdateProductRequestDTO(
        @Size(min = 2, max = 50)
        String name,

        @DecimalMin("0.0")
        @Digits(integer = 6, fraction = 2)
        Double price,

        @Size(min = 2)
        String description,

        String urlImage,

        @Max(value = 9999)
        Integer stock,

        Integer categoryId
) {}
