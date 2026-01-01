package com.vexa.ecommerce.Payment;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Orders.Orders;
import com.vexa.ecommerce.Orders.OrdersService;
import com.vexa.ecommerce.Orders.OrdersStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentService {

    private final OrdersService ordersService;
    private final StripeClient stripeClient;

    public PaymentService(OrdersService ordersService, StripeClient stripeClient) {
        this.ordersService = ordersService;
        this.stripeClient = stripeClient;
    }

    public String createIntent(Integer orderId) {
        // Obtener el order mediante el OrderId
        Orders order = ordersService.getOrderByOrderId(orderId);
        // Si existe comprobamos el status que esté en pending
        if (order.getStatus() != OrdersStatus.PENDING) {
            // Lanzamos excepción bad request si el status no es pending
            log.warn("The status of the order with order ID {} is not PENDING. order.staus: {}", order.getOrderId(), order.getStatus());
            throw new BadRequestException("The status of the order with order id: " + orderId + " is not pending. order.staus:" + order.getStatus());
        }

        // Variables necesarias para crear PaymentIntent
        long amount = new BigDecimal(order.getTotalPrice()).multiply(new BigDecimal(100)).longValueExact();
        String currency = "eur";

        // Creamos el PaymentIntent utilizando los params
        PaymentIntent paymentIntent = stripeClient.createPaymentIntent(amount, currency);

        // Guardamos el payment intent id en la order
        order.setPaymentIntentId(paymentIntent.getId());
        ordersService.saveOrder(order);

        // Extraemos el secret client key del PaymentIntent creado y lo retornamos.
        log.info("PaymentIntentID has been saved for order with ID {} and clientSecret has been returned", order.getOrderId());
        return paymentIntent.getClientSecret();
    }

    public void handleWebhook(String payload, String signature) {
        // Comprobar Firma signature
        Event event = stripeClient.parseWebhook(payload, signature);

        // Comprobamos si el payment intent es de tipo succeeded, en caso de no serlo, terminamos el proceso
        if (!"payment_intent.succeeded".equals(event.getType())) {
            return;
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isEmpty()) {
            log.error("[STRIPE WEBHOOK] Failed to deserialize event. eventId={}, type={}, apiVersion={}",
                    event.getId(), event.getType(), event.getApiVersion());
            return;
        }

        // Obtener PaymentIntent
        PaymentIntent paymentIntent = (PaymentIntent) deserializer.getObject().get();
        Orders order = ordersService.getOrderByPaymentIntentId(paymentIntent.getId());

        if (order.getStatus() == OrdersStatus.PENDING) {
            order.setStatus(OrdersStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            ordersService.saveOrder(order);
            log.info("Order status changed to PAID and paidAt has been saved for order with ID {}", order.getOrderId());
        }
    }
}
