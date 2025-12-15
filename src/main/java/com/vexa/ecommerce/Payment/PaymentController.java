package com.vexa.ecommerce.Payment;
import com.vexa.ecommerce.Orders.Orders;
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
    public ResponseEntity<String> createIntent(@RequestBody Integer orderId) { // Viene el order para saber de qué order crear el intent
        String secretClientKey = paymentService.createIntent(orderId);
        return ResponseEntity.ok(secretClientKey);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> chekPayment(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        paymentService.handleWebhook(payload, signature);
        // Si esta todo bien se retorna 200
        return ResponseEntity.noContent().build();
    }

}
