package com.vexa.ecommerce.Products.DTOs;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Integer productId;
    private String name;
    private Double price;
    private String description;
    private String urlImage;
    private Integer stock;
}
