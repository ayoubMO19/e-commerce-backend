package com.vexa.ecommerce.Exceptions;

public class StripePaymentIntentException extends RuntimeException {
        public StripePaymentIntentException(String message, Throwable cause) {
            super(message, cause);
        }
}
