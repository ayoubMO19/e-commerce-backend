package com.vexa.ecommerce.Cart.DTOs;

import lombok.Data;

@Data
public class CartUpdateRequestDTO {
    private Integer userId;
    private Integer productId;
    private Integer quantity;
}
