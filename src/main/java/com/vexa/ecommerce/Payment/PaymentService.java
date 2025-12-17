package com.vexa.ecommerce.Payment;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Orders.Orders;
import com.vexa.ecommerce.Orders.OrdersService;
import com.vexa.ecommerce.Orders.OrdersStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final OrdersService ordersService;

    @Value("${stripe.secret-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public PaymentService(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public String createIntent(Integer orderId) {
        // Obtener el order mediante el OrderId
        Orders order = ordersService.getOrderByOrderId(orderId);
        // Si existe comprobamos el status que esté en pending
        if (order.getStatus() != OrdersStatus.pending) {
            // Lanzamos excepción bad request si el status no es pending
            throw new BadRequestException("The status of the order with order id: " + orderId + " is not pending. order.staus:" + order.getStatus());
        }

        // Variables necesarias para crear PaymentIntent
        long amount = new BigDecimal(order.getTotalPrice()).multiply(new BigDecimal(100)).longValueExact();
        String currency = "eur";

        // Creamos el PaymentIntent utilizando los params
        try {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setAutomaticPaymentMethods( // Métodos de pago automáticos por Stripe
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        ).putMetadata("orderId", orderId.toString()) // Aquí incluimos el orderId en metadata
                        .build();


            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Extraemos el secret client key del PaymentIntent creado y lo retornamos.
            return paymentIntent.getClientSecret();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> handleWebhook(String payload, String signature) {
        try{
            // Comprobar Firma signature
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);

            // Comprobamos si el payment intent es de tipo succeeded, en caso de no serlo, terminamos el proceso
            if (!event.getType().equals("payment_intent.succeeded")) {
                return ResponseEntity.ok().build();
            }

            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isEmpty()) {
                System.err.println(
                        "[STRIPE WEBHOOK] Failed to deserialize event. " +
                                "eventId=" + event.getId() +
                                ", type=" + event.getType() +
                                ", apiVersion=" + event.getApiVersion()
                );

                // Return 200
                return ResponseEntity.ok().build();
            }

            stripeObject = dataObjectDeserializer.getObject().get();

            // Obtener PaymentIntent
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            // Obtener orderId de los metadatos de PaymentIntent
            Integer orderId = Integer.parseInt(paymentIntent.getMetadata().get("orderId"));
            // Buscar el order por orderId
            Orders order = ordersService.getOrderByOrderId(orderId);
            if (order.getStatus() == OrdersStatus.pending) { // Comprobamos si order existe y su estado
                // Cambiamos estado de order a PAID
                order.setStatus(OrdersStatus.paid);
                ordersService.updateOrder(order);
            }

        } catch (SignatureVerificationException e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            System.err.println("Header: " + e.getSigHeader());
            return ResponseEntity.badRequest().build();
        }
        // retornamos 200 ok
        return ResponseEntity.ok().build();
    }
}
