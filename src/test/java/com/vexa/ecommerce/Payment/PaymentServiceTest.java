package com.vexa.ecommerce.Payment;

import com.stripe.model.Event;
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
        Assertions.assertEquals("test_client_secret", secretClient);
    }

    @Test
    void handleWebhook() {
        // Preparación de datos
        Event event = new Event(); // ¿Cómo creo un evento correcto para que pase por mi flujo de handleWebhook?
        event.setType("payment_intent.succeeded");
        Event.Data data = new Event.Data(); // Esto está mal, lo he hecho siguiendo mi intuición
        event.setData(data);
        Orders order = createOrder(1);

        // Ejecución de lógica
        when(stripeClient.parseWebhook("payload", "signature")).thenReturn(event);
        when(ordersService.getOrderByPaymentIntentId("paymentIntentId")).thenReturn(order);
        when(ordersService.saveOrder(order)).thenReturn(order);
        paymentService.handleWebhook("payload", "signature");

        // Comprobaciones del resultado
        verify(stripeClient.parseWebhook("payload", "signature"), times(1));
        verify(ordersService.getOrderByPaymentIntentId("paymentIntentId"), times(1));
        verify(ordersService.saveOrder(order), times(1));
    }
}