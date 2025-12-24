package com.vexa.ecommerce.Payment;

import com.vexa.ecommerce.Payment.DTOs.PaymentIntentRequestDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Payment", description = "Endpoints para gestionar pagos")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "(STRIPE) - Crear nuevo intento de pago",
            description = "Crear un nuevo intento de pago de stripe",
            tags = {"Payment"}
    )
    @ApiResponse(responseCode = "200", description = "PaymentIntent creado con éxito existosamente") // Respuesta exitosa
    @PostMapping("/create-intent")
    public ResponseEntity<String> createIntent(@Valid @RequestBody PaymentIntentRequestDTO orderDetails) {
        String secretClientKey = paymentService.createIntent(orderDetails.orderId());
        return ResponseEntity.ok(secretClientKey);
    }

    @PostMapping("/webhook")
    @Hidden
    public ResponseEntity<?> chekPayment(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        // Return result code
        return paymentService.handleWebhook(payload, signature);
    }

}
