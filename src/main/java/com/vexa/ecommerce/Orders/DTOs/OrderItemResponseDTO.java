package com.vexa.ecommerce.Orders.DTOs;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
}
