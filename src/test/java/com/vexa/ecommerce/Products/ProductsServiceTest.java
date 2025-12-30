package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Categories.Categories;
import com.vexa.ecommerce.Categories.CategoriesService;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Products.DTOs.UpdateProductRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    ProductsRepository productsRepository;
    @Mock
    CategoriesService categoriesService;
    @InjectMocks
    ProductsService productsService;

    private Products createProduct(Integer id) {
        Products p = new Products("name", 10.0, "description", "url_image", 10);
        p.setProductId(id);
        return p;
    }

    @Test
    void getAllProducts_shouldReturnProducts() {
        List<Products> productsList = List.of(createProduct(1), createProduct(2));

        // Ejecución de lógica
        when(productsRepository.findAll()).thenReturn(productsList);
        List<Products> obtainedProductsList = productsService.getAllProducts();

        // Comprobaciones de resultado
        assertEquals(productsList.size(), obtainedProductsList.size());
        Assertions.assertEquals(productsList.get(0).getProductId(), obtainedProductsList.get(0).getProductId());
    }

    @Test
    void saveNewProduct_ShouldSaveProduct() {
        // Preparación de datos
        Products product = createProduct(1);

        // Ejecución de lógica
        when(productsRepository.save(product)).thenReturn(product);
        Products addedProduct = productsService.saveNewProduct(product);

        // Comprobaciones de resultado
        verify(productsRepository, times(1)).save(product);
        assertNotNull(addedProduct); // Comprobar que la respuesta al guardado de producto no es null
        assertEquals(product.getProductId(), addedProduct.getProductId()); // Comprobar que el ID del producto original y el guardado es el mismo
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        // Preparación de datos
        Products product = createProduct(1);
        Optional<Products> productsOptional = Optional.of(product);

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(productsOptional);
        Products obtainedProduct = productsService.getProductById(product.getProductId());

        // Comprobaciones de resultado
        assertNotNull(obtainedProduct);
        assertEquals(productsOptional.get().getProductId(), obtainedProduct.getProductId());
    }

    @Test
    void getProductById_shouldThrowException_whenProductDoesNotExist() {
        // Preparación de datos
        Products product = createProduct(1);

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, ()-> {
            productsService.getProductById(product.getProductId());
        });

        // Comprobaciones del resultado
        assertEquals("Product with id 1 not found", resourceNotFoundException.getMessage());
    }


    @Test
    void updateProduct_shouldUpdateSuccessfully() {
        // Preparación de datos
        Products product = createProduct(1);
        Optional<Products> productsOptional = Optional.of(product);
        UpdateProductRequestDTO dto = new UpdateProductRequestDTO("nameUpdated", 1.0, "description", "urlImage", 10, 1);
        Categories category = new Categories("categoryName");

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(productsOptional);
        when(productsRepository.existsByNameAndProductIdNot(dto.name(), product.getProductId())).thenReturn(false);
        when(categoriesService.getCategoryById(dto.categoryId())).thenReturn(category);
        when(productsRepository.save(any(Products.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        Products updatedProduct = productsService.updateProduct(product.getProductId(), dto);

        // Verificación de llamadas a funciones
        verify(productsRepository).findById(1);
        verify(productsRepository).existsByNameAndProductIdNot("nameUpdated", 1);
        verify(categoriesService).getCategoryById(1);
        verify(productsRepository).save(any(Products.class));

        // Comprobaciones de resultado
        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals(product.getProductId(), updatedProduct.getProductId());
        Assertions.assertEquals("nameUpdated", updatedProduct.getName());
        Assertions.assertEquals(1.0, updatedProduct.getPrice());
        Assertions.assertEquals(10, updatedProduct.getStock());
        Assertions.assertEquals(category, updatedProduct.getCategory());
    }

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {
        // Preparación de datos
        Products product = createProduct(1);
        UpdateProductRequestDTO dto = new UpdateProductRequestDTO("nameUpdated", 1.0, "description", "urlImage", 10, 1);

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productsService.updateProduct(product.getProductId(), dto);
        });
        // Verificación de llamadas a funciones
        verify(productsRepository).findById(1);

        // Comprobaciones de resultado
        Assertions.assertEquals("Product with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateProduct_shouldThrowException_whenCategoryNotFound() {
        // Preparación de datos
        Products product = createProduct(1);
        Optional<Products> productsOptional = Optional.of(product);
        UpdateProductRequestDTO dto = new UpdateProductRequestDTO("nameUpdated", 1.0, "description", "urlImage", 10, 1);

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(productsOptional);
        when(productsRepository.existsByNameAndProductIdNot(dto.name(), product.getProductId())).thenReturn(false);
        when(categoriesService.getCategoryById(1))
                .thenThrow(new ResourceNotFoundException("Category", 1));

        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productsService.updateProduct(product.getProductId(), dto);
        });

        // Verificación de llamadas a funciones
        verify(productsRepository).findById(1);
        verify(productsRepository).existsByNameAndProductIdNot("nameUpdated", 1);
        verify(categoriesService).getCategoryById(1);
        verify(productsRepository, never()).save(any());

        // Comprobaciones de resultado
        Assertions.assertEquals("Category with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void deleteProductById_shouldDelete_whenProductWExists() {
        // Preparación de datos
        Products product = createProduct(1);

        // Ejecución de lógica
        when(productsRepository.existsById(product.getProductId())).thenReturn(true);
        doNothing().when(productsRepository).deleteById(product.getProductId());
        productsService.deleteProductById(product.getProductId());

        // Comprobaciones de resultado
        verify(productsRepository, times(1)).deleteById(product.getProductId());
    }

    @Test
    void deleteProductById_shouldThrowException_whenProductNotFound() {
        // Preparación de datos
        Products product = createProduct(1);

        // Ejecución de lógica
        when(productsRepository.existsById(product.getProductId())).thenReturn(false);
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productsService.deleteProductById(product.getProductId());
        });

        // Comprobaciones de resultados
        Assertions.assertEquals("Product with id 1 not found", resourceNotFoundException.getMessage());
    }
}