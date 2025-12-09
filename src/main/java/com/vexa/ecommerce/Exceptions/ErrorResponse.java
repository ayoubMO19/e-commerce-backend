package com.vexa.ecommerce.Exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String type,
        LocalDateTime timestamp
) {}
