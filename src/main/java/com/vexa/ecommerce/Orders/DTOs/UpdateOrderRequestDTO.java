package com.vexa.ecommerce.Orders.DTOs;

import com.vexa.ecommerce.Orders.OrdersStatus;
import java.time.LocalDateTime;

public record UpdateOrderRequestDTO (
        OrdersStatus status,
        String shippingAddress,
        String paymentIntentId,
        LocalDateTime paidAt
) {}
