package com.vexa.ecommerce.Payment;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.vexa.ecommerce.Exceptions.StripePaymentIntentException;
import com.vexa.ecommerce.Exceptions.StripeWebhookSignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StripeClientImpl implements StripeClient {

    @Value("${stripe.secret-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentIntent createPaymentIntent(long amount, String currency) {
        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amount)
                            .setCurrency(currency)
                            .setAutomaticPaymentMethods( // Métodos de pago automáticos por Stripe
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            ).build();

            return PaymentIntent.create(params);
        } catch (Exception e) {
            log.error("Error creating PaymentIntent, error: {}", e.getMessage());
            throw new StripePaymentIntentException("Error creating PaymentIntent", e);
        }
    }

    @Override
    public Event parseWebhook(String payload, String signature) {
        try {
            return Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature");
            throw new StripeWebhookSignatureException("Invalid webhook signature", e);
        }
    }
}

