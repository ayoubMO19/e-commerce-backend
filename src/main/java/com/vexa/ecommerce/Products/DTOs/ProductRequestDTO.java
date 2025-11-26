package com.vexa.ecommerce.Products.DTOs;

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

    private Integer stock;

    private Integer categoryId;

}

