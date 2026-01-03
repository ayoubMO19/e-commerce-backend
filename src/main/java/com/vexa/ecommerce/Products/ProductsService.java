package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Categories.Categories;
import com.vexa.ecommerce.Categories.CategoriesService;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Products.DTOs.UpdateProductRequestDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductsService implements IProductsService {
    private final ProductsRepository productsRepository;
    private final CategoriesService categoriesService;

    public ProductsService(ProductsRepository productsRepository, CategoriesService categoriesService) {
        this.productsRepository = productsRepository;
        this.categoriesService = categoriesService;
    }

    @Override
    public List<Products> getAllProducts() {
        return this.productsRepository.findAll();
    }

    @Override
    public Products getProductById(Integer id) {
        return this.productsRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id {} not found", id);
            return new ResourceNotFoundException("Product", id);
        });
    }

    @Override
    public Products saveNewProduct(Products product, Integer categoryId) {
        Categories category = categoriesService.getCategoryById(categoryId);
        product.setCategory(category);
        if (productsRepository.existsByName(product.getName())) {
            log.warn("Product Name {} already exists. The product could not be saved", product.getName());
            throw new BadRequestException("Product name already exists.");
        }

        log.info("Product with name {} has been created", product.getName());
        return this.productsRepository.save(product);
    }

    @Override
    @Transactional
    public Products updateProduct(Integer productId, UpdateProductRequestDTO dto) {
        // Comprobar si Existe
        Products product = this.productsRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product with id {} not found. The product could not be updated", productId);
                    return new ResourceNotFoundException("Product", productId);
                });

        if (dto.name() != null) {
            // Comprobar Nombre duplicado (excepto si es el mismo producto)
            if (productsRepository.existsByNameAndProductIdNot(dto.name(), productId)) {
                log.warn("Product Name {} already exists. The product could not be updated", dto.name());
                throw new BadRequestException("Product name already exists.");
            }
            product.setName(dto.name());
        }

        if (dto.price() != null) {
            product.setPrice(dto.price());
        }

        if (dto.stock() != null) {
            product.setStock(dto.stock());
        }

        if (dto.urlImage() != null) {
            product.setUrlImage(dto.urlImage());
        }

        if (dto.description() != null) {
            product.setDescription(dto.description());
        }

        // Obtener la category y comprobar si existe
        if (dto.categoryId() != null) {
            Categories category = categoriesService.getCategoryById(dto.categoryId()); // No manejamos la existencia de la categoria ya que se maneja en el service
            product.setCategory(category);

        }

        log.info("Product with id {} has been updated", productId);
        // Guardar actualización de producto
        return this.productsRepository.save(product);
    }

    @Override
    public void deleteProductById(Integer id) {
        if(!this.productsRepository.existsById((id))) {
            log.warn("Product id {} not found. The product could not be deleted", id);
            throw new ResourceNotFoundException("Product", id);
        }
        this.productsRepository.deleteById(id);
        log.info("Product with id {} has been deleted", id);
    }
}
