package com.vexa.ecommerce.Orders.DTOs;

import com.vexa.ecommerce.Orders.OrdersStatus;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Integer orderId;
    private OrdersStatus status;
    private Double totalPrice;
    private String shippingAddress;
    private Date createdAt;
    private List<OrderItemResponseDTO> items;
}
