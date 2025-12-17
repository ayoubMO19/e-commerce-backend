package com.vexa.ecommerce.Cart.DTOs;

import lombok.Data;

@Data
public class CartAddRequestDTO {
    private Integer productId;
    private Integer quantity;
}
