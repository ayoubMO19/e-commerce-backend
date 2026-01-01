package com.vexa.ecommerce.Payment;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;

public interface StripeClient {
    PaymentIntent createPaymentIntent(long amount, String currency);
    Event parseWebhook(String payload, String signature);
}
