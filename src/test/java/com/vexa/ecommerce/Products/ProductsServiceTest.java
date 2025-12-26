package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
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
    @InjectMocks
    ProductsService productsService;

    @Test
    void getAllProducts_ShouldGotProducts() {
        // Preparación de datos
        Products product = new Products("name", 10.0, "description" , "url_image", 10);
        product.setProductId(1);
        Products product2 = new Products("name2", 20.0, "description2" , "url_image2", 20);
        product2.setProductId(2);
        List<Products> productsList = List.of(product, product2);

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
        Products product = new Products("name", 10.0, "description", "url_image", 10);
        product.setProductId(1);

        // Ejecución de lógica
        when(productsRepository.save(product)).thenReturn(product);
        Products addedProduct = productsService.saveNewProduct(product);

        // Comprobaciones de resultado
        assertNotNull(addedProduct); // Comprobar que la respuesta al guardado de producto no es null
        assertEquals(product.getProductId(), addedProduct.getProductId()); // Comprobar que el ID del producto original y el guardado es el mismo
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        // Preparación de datos
        Products product = new Products();
        product.setProductId(1);
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
        Products product = new Products();
        product.setProductId(1);

        // Ejecución de lógica
        when(productsRepository.findById(product.getProductId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, ()-> {
            productsService.getProductById(product.getProductId());
        });

        // Comprobaciones del resultado
        Assertions.assertEquals("Product with id 1 not found", resourceNotFoundException.getMessage());
    }


    @Test
    void updateProduct_shouldUpdateSuccessfully() {
        // Preparación de datos
        Products product = new Products("name", 10.0, "description", "url_image", 10);
        product.setProductId(1);
        Optional<Products> productsOptional = Optional.of(product);

        // TODO: Hay que revisar la función updateProduct del service ya que puede ser necesario modificarla primero
        // Ejecución de lógica
        // Comprobaciones de resultado
    }

    @Test
    void deleteProductById_shouldDelete_whenProductWExists() {
        // Preparación de datos
        Integer productId = 1;

        // Ejecución de lógica
        when(productsRepository.existsById(1)).thenReturn(true);
        doNothing().when(productsRepository).deleteById(productId);
        productsService.deleteProductById(productId);

        // Comprobaciones de resultado
        verify(productsRepository, times(1)).deleteById(productId);
    }

    @Test
    void deleteProductById_shouldThrowException_whenProductNotFound() {
        // Preparación de datos
        Integer productId = 1;

        // Ejecución de lógica
        when(productsRepository.existsById(1)).thenReturn(false);
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productsService.deleteProductById(productId);
        });

        // Comprobaciones de resultados
        Assertions.assertEquals("Product with id 1 not found", resourceNotFoundException.getMessage());
    }
}