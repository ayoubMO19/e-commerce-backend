package com.vexa.ecommerce.Orders.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdersRequestDTO {

    @NotNull
    private Integer userId;

    @NotBlank(message = "shippingAddress cannot be empty")
    private String shippingAddress;
}
