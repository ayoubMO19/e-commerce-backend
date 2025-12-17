package com.vexa.ecommerce.Payment;

import com.vexa.ecommerce.Payment.DTOs.PaymentIntentRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<String> createIntent(@Valid @RequestBody PaymentIntentRequestDTO orderDetails) {
        String secretClientKey = paymentService.createIntent(orderDetails.orderId());
        return ResponseEntity.ok(secretClientKey);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> chekPayment(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        // Return result code
        return paymentService.handleWebhook(payload, signature);
    }

}
