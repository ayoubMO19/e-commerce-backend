package com.vexa.ecommerce.Exceptions;

public class StripeWebhookSignatureException extends RuntimeException {
    public StripeWebhookSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
