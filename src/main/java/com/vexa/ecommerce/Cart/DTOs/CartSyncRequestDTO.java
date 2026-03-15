package com.vexa.ecommerce.Cart.DTOs;

import lombok.Data;
import java.util.List;

@Data
public class CartSyncRequestDTO {
    private List<CartItemSyncDTO> items;

    @Data
    public static class CartItemSyncDTO {
        private Integer productId;
        private Integer quantity;
    }
}