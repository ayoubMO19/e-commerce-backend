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
        return this.productsRepository.save(product);
    }

    @Override
    public Products updateProduct(Products product) {
        this.productsRepository.findById(product.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", product.getProductId()));

        return this.productsRepository.save(product);
    }

    @Override
    public void deleteProductById(Integer id) {
        if(!this.productsRepository.existsById((id))) {
            throw new ResourceNotFoundException("Product", id);
        }
        this.productsRepository.deleteById(id);
    }
}
