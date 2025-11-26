package com.vexa.ecommerce.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String type,
        LocalDateTime timestamp
) {}
