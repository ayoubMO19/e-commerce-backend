package com.vexa.ecommerce.Cart.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private Integer userId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private Double totalPrice;

    public CartResponseDTO(Integer userId, List<CartItemDTO> items) {
        this.userId = userId;
        this.items = items;
        this.totalItems = items.stream().mapToInt(CartItemDTO::getQuantity).sum();
        this.totalPrice = items.stream().mapToDouble(CartItemDTO::getTotal).sum();
    }
}
