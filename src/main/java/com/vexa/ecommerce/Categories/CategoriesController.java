package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Categories.DTOs.CategoriesRequestDTO;
import com.vexa.ecommerce.Categories.DTOs.CategoriesResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDTO>> getAllCategories() {
        List<Categories> categories = categoriesService.getAllCategories();
        List<CategoriesResponseDTO> dtoList = categories.stream()
                .map(CategoriesMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriesResponseDTO> getCategoryById(@PathVariable Integer id) {
        Categories category = categoriesService.getCategoryById(id);
        return ResponseEntity.ok(CategoriesMapper.toDTO(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriesResponseDTO> createCategory(@Valid @RequestBody CategoriesRequestDTO requestDTO) {
        Categories newCategory = CategoriesMapper.toEntity(requestDTO);
        Categories savedCategory = categoriesService.saveNewCategory(newCategory);

        return ResponseEntity.ok(CategoriesMapper.toDTO(savedCategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriesResponseDTO> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriesRequestDTO requestDTO
    ) {
        Categories updatedCategory = CategoriesMapper.toEntity(requestDTO);
        updatedCategory.setCategoryId(id);

        Categories savedCategory = categoriesService.updateCategory(updatedCategory);
        return ResponseEntity.ok(CategoriesMapper.toDTO(savedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoriesService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
