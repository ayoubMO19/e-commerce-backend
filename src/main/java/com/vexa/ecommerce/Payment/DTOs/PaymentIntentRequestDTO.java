package com.vexa.ecommerce.Payment.DTOs;

import jakarta.validation.constraints.NotNull;

public record PaymentIntentRequestDTO (
    @NotNull
    Integer orderId
) {}
