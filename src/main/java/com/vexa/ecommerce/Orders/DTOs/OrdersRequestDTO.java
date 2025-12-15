package com.vexa.ecommerce.Orders.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrdersRequestDTO {

    @NotNull(message = "Shipping address cannot be null")
    @Size(min = 5, max = 200, message = "Shipping address must be between 5 and 200 characters")
    private String shippingAddress;
}
