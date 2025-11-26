package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Categories.Categories;
import com.vexa.ecommerce.Products.DTOs.ProductRequestDTO;
import com.vexa.ecommerce.Products.DTOs.ProductResponseDTO;

public class ProductMapper {

    public static Products toEntity(ProductRequestDTO dto) {
        Products product = new Products();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setUrlImage(dto.getUrlImage());
        product.setStock(dto.getStock());

        // si categoryId viene, asignas categoría
        if (dto.getCategoryId() != null) {
            Categories category = new Categories();
            category.setCategoryId(dto.getCategoryId());
            product.setCategory(category);
        }

        return product;
    }

    public static ProductResponseDTO toDTO(Products product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setUrlImage(product.getUrlImage());
        dto.setStock(product.getStock());
        return dto;
    }
}
