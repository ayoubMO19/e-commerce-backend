package com.vexa.ecommerce.Products.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    private String description;

    private String urlImage;

    @Max(value = 9999)
    private Integer stock;

    private Integer categoryId;

}

