package com.vexa.ecommerce.Products;

import java.util.List;

public interface IProductsService {

    List<Products> getAllProducts();

    Products getProductById(Integer id);

    Products saveNewProduct(Products product);

    Products updateProduct(Products product);

    void deleteProductById(Integer id);
}
