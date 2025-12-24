package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Categories.DTOs.CategoriesRequestDTO;
import com.vexa.ecommerce.Categories.DTOs.CategoriesResponseDTO;
import com.vexa.ecommerce.Users.DTOs.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    // ADMIN ENDPOINTS
    @Operation(
            summary = "(ADMIN) - Crear una nueva categoría",
            description = "Crear una nueva categoría",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Categoría creada existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriesResponseDTO.class))) // Respuesta exitosa
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriesResponseDTO> createCategory(@Valid @RequestBody CategoriesRequestDTO requestDTO) {
        Categories newCategory = CategoriesMapper.toEntity(requestDTO);
        Categories savedCategory = categoriesService.saveNewCategory(newCategory);

        return ResponseEntity.ok(CategoriesMapper.toDTO(savedCategory));
    }

    @Operation(
            summary = "(ADMIN) - Actualizar una categoría",
            description = "Actualizar una categoría",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Categoría actualizada existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriesResponseDTO.class))) // Respuesta exitosa
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

    @Operation(
            summary = "(ADMIN) - Eliminar una categoría",
            description = "Eliminar una categoría",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Categoría eliminada existosamente") // Respuesta exitosa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoriesService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }


    // CLIENTS ENDPOINTS
    @Tag(name = "Categories", description = "Endpoints para gestionar categorías")
    @Operation(
            summary = "Obtener todas las categorías",
            description = "Obtener todas las categorías",
            tags = {"Categories"}
    )
    @ApiResponse(responseCode = "200", description = "Categorías obtenidas existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriesResponseDTO.class))) // Respuesta exitosa
    @GetMapping
    public ResponseEntity<List<CategoriesResponseDTO>> getAllCategories() {
        List<Categories> categories = categoriesService.getAllCategories();
        List<CategoriesResponseDTO> dtoList = categories.stream()
                .map(CategoriesMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Operation(
            summary = "Obtener una categoría por id",
            description = "Obtener una categoría por id",
            tags = {"Categories"}
    )
    @ApiResponse(responseCode = "200", description = "Categoría obtenida existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriesResponseDTO.class))) // Respuesta exitosa
    @GetMapping("/{id}")
    public ResponseEntity<CategoriesResponseDTO> getCategoryById(@PathVariable Integer id) {
        Categories category = categoriesService.getCategoryById(id);
        return ResponseEntity.ok(CategoriesMapper.toDTO(category));
    }
}
