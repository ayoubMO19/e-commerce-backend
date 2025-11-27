package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Products.DTOs.ProductRequestDTO;
import com.vexa.ecommerce.Products.DTOs.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Products> products = productsService.getAllProducts();
        List<ProductResponseDTO> dtoList = products.stream()
                .map(ProductMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        Products product = productsService.getProductById(id);
        return ResponseEntity.ok(ProductMapper.toDTO((product)));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        Products newProduct = ProductMapper.toEntity(requestDTO);
        Products savedProduct = productsService.saveNewProduct(newProduct);

        return ResponseEntity.ok(ProductMapper.toDTO(savedProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequestDTO requestDTO
    ) {
        Products updatedProduct = ProductMapper.toEntity(requestDTO);
        updatedProduct.setProductId(id);

        Products savedProduct = productsService.updateProduct(updatedProduct);
        return ResponseEntity.ok(ProductMapper.toDTO(savedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productsService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
