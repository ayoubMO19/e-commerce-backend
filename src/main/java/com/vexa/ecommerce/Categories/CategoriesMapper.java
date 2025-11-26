package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Categories.DTOs.CategoriesRequestDTO;
import com.vexa.ecommerce.Categories.DTOs.CategoriesResponseDTO;

public class CategoriesMapper {

    public static Categories toEntity(CategoriesRequestDTO dto) {
        Categories category = new Categories();
        category.setName(dto.getName());

        return category;
    }

    public static CategoriesResponseDTO toDTO(Categories category) {
        CategoriesResponseDTO dto = new CategoriesResponseDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());

        return dto;
    }
}
