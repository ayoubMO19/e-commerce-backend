package com.vexa.ecommerce.Payment;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.vexa.ecommerce.Orders.Orders;
import com.vexa.ecommerce.Orders.OrdersService;
import com.vexa.ecommerce.Orders.OrdersStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    OrdersService ordersService;
    @Mock
    StripeClient stripeClient;
    @InjectMocks
    PaymentService paymentService;

    private Orders createOrder(Integer id) {
        Orders o = new Orders(OrdersStatus.PENDING, 10.0, "shippingAddress", LocalDateTime.now(), LocalDateTime.now(), "paymentIntentId", LocalDateTime.now());
        o.setOrderId(id);
        return o;
    }

    @Test
    void createIntent() {
        // Preparación de datos
        Orders order = createOrder(1);
        order.setPaymentIntentId(null);
        long amount = new BigDecimal(order.getTotalPrice()).multiply(new BigDecimal(100)).longValueExact();
        PaymentIntent fakePaymentIntent = new PaymentIntent();
        fakePaymentIntent.setAmount(amount);
        fakePaymentIntent.setCurrency("eur");
        fakePaymentIntent.setClientSecret("test_client_secret");

        // Ejecución de lógica
        when(ordersService.getOrderByOrderId(order.getOrderId())).thenReturn(order);
        when(stripeClient.createPaymentIntent(amount, "eur")).thenReturn(fakePaymentIntent);
        when(ordersService.saveOrder(order)).thenReturn(order);
        String secretClient = paymentService.createIntent(order.getOrderId());

        // Comprobaciones del resultado
        assertNotNull(secretClient);
        assertEquals("test_client_secret", secretClient);
        assertEquals(fakePaymentIntent.getId(), order.getPaymentIntentId());
        verify(stripeClient).createPaymentIntent(amount, "eur");
        verify(ordersService).saveOrder(order);
    }

    @Test
    void handleWebhook() {
        // Preparación de datos
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        Orders order = createOrder(1);

        // Ejecución de lógica
        when(stripeClient.parseWebhook("payload", "signature")).thenReturn(event);
        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(java.util.Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("paymentIntentId");
        when(ordersService.getOrderByPaymentIntentId("paymentIntentId")).thenReturn(order);
        paymentService.handleWebhook("payload", "signature");

        // Comprobaciones del resultado
        assertEquals(OrdersStatus.PAID, order.getStatus());
        assertNotNull(order.getPaidAt());
        verify(ordersService).saveOrder(order);
    }
}