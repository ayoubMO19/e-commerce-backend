package com.vexa.ecommerce.Products;

import com.vexa.ecommerce.Products.DTOs.UpdateProductRequestDTO;

import java.util.List;

public interface IProductsService {

    List<Products> getAllProducts();

    Products getProductById(Integer id);

    Products saveNewProduct(Products product);

    Products updateProduct(Integer id, UpdateProductRequestDTO dto);

    void deleteProductById(Integer id);
}
