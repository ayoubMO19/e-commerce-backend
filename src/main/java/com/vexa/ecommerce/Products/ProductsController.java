package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Categories.CategoriesRepository;
import com.vexa.ecommerce.Products.DTOs.ProductRequestDTO;
import com.vexa.ecommerce.Products.DTOs.ProductResponseDTO;
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
@RequestMapping("/api/products")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductsController {

    private final ProductsService productsService;
    private final CategoriesRepository categoriesRepository;

    public ProductsController(ProductsService productsService, CategoriesRepository categoriesRepository) {
        this.productsService = productsService;
        this.categoriesRepository = categoriesRepository;
    }

    // ENDPOINTS PARA ROL ADMIN
    @Operation(
            summary = "(ADMIN) - Crear un nuevo producto",
            description = "Crear un nuevo producto en el sistema",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Producto creado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))) // Respuesta exitosa
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        // Comprobar si la categoría existe
        categoriesRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Products newProduct = ProductMapper.toEntity(requestDTO);
        Products savedProduct = productsService.saveNewProduct(newProduct);

        return ResponseEntity.ok(ProductMapper.toDTO(savedProduct));
    }

    @Operation(
            summary = "(ADMIN) - Actualizar un producto",
            description = "Actualizar un producto del sistema",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Producto actualizado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))) // Respuesta exitosa
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequestDTO requestDTO
    ) {
        // Comprobar si la categoría existe
        categoriesRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Products updatedProduct = ProductMapper.toEntity(requestDTO);
        updatedProduct.setProductId(id);

        Products savedProduct = productsService.updateProduct(updatedProduct);
        return ResponseEntity.ok(ProductMapper.toDTO(savedProduct));
    }

    @Operation(
            summary = "(ADMIN) - Eliminar un producto",
            description = "Eliminar un producto en el sistema",
            tags = {"Admin Endpoints"}
    )
    @ApiResponse(responseCode = "200", description = "Producto eliminado existosamente") // Respuesta exitosa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productsService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }


    // ENDPOINTS PARA ROL USER
    @Tag(name = "Products", description = "Endpoints para obtener productos")
    @Operation(
            summary = "Obtener productos",
            description = "Obtener todos los productos del sistema",
            tags = {"Products"}
    )
    @ApiResponse(responseCode = "200", description = "Products obtenidos existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))) // Respuesta exitosa
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Products> products = productsService.getAllProducts();
        List<ProductResponseDTO> dtoList = products.stream()
                .map(ProductMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Operation(
            summary = "Obtener producto por id",
            description = "Obtener producto por id",
            tags = {"Products"}
    )
    @ApiResponse(responseCode = "200", description = "Producto obtenido existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))) // Respuesta exitosa
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        Products product = productsService.getProductById(id);
        return ResponseEntity.ok(ProductMapper.toDTO((product)));
    }
}
