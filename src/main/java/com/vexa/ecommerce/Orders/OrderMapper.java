package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrderItemResponseDTO;
import com.vexa.ecommerce.Orders.DTOs.OrderResponseDTO;

public class OrderMapper {

    public static OrderItemResponseDTO toItemDTO(OrderItems item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        return dto;
    }

    public static OrderResponseDTO toDTO(Orders order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());

        dto.setItems(
                order.getOrderItemsList().stream()
                        .map(OrderMapper::toItemDTO)
                        .toList()
        );

        return dto;
    }

}
