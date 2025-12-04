package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService implements IProductsService {
    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public List<Products> getAllProducts() {
        return this.productsRepository.findAll();
    }

    @Override
    public Products getProductById(Integer id) {
        return this.productsRepository.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException("Product", id);
        });
    }

    @Override
    public Products saveNewProduct(Products product) {
        if (productsRepository.existsByName(product.getName())) {
            throw new RuntimeException("Product name already exists.");
        }
        return this.productsRepository.save(product);
    }

    @Override
    public Products updateProduct(Products product) {
        // Comprobar si Existe
        Products existing = this.productsRepository.findById(product.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", product.getProductId()));

        // Comprobar Nombre duplicado (excepto si es el mismo producto)
        if (productsRepository.existsByNameAndProductIdNot(product.getName(), product.getProductId())) {
            throw new RuntimeException("Product name already exists.");
        }

        // Actualizar campos
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());

        return this.productsRepository.save(existing);
    }


    @Override
    public void deleteProductById(Integer id) {
        if(!this.productsRepository.existsById((id))) {
            throw new ResourceNotFoundException("Product", id);
        }
        this.productsRepository.deleteById(id);
    }
}
