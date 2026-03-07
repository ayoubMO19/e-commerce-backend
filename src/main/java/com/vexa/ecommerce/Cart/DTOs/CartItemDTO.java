package com.vexa.ecommerce.Cart.DTOs;

import lombok.Data;

@Data
public class CartItemDTO {
    private Integer productId;
    private String name;
    private Double price;
    private String urlImage;
    private Integer stock;
    private Integer quantity;
    private Double total;

    public CartItemDTO(Integer productId, String name, Double price, String urlImage, Integer stock, Integer quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.urlImage = urlImage;
        this.stock = stock;
        this.quantity = quantity;
        this.total = price * quantity;
    }
}
