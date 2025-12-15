package com.vexa.ecommerce.Payment;

import com.vexa.ecommerce.Orders.Orders;
import com.vexa.ecommerce.Orders.OrdersService;
import com.vexa.ecommerce.Orders.OrdersStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrdersService ordersService;

    public PaymentService(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    public String createIntent(Integer orderId) {
        // Obtener el order mediante el OrderId
        Orders order = ordersService.getOrderByOrderId(orderId);
        // Si existe comprobamos el status que este en pending para poder continuar
        if (order.getStatus() != OrdersStatus.pending) {
            // No se como retornar error
        }
        // Creamos el PaymentIntent utilizando monda euro,
        // order.totalPrice en centimos, método de pago de momento solo ponemos card. Con eso de momento es suficiente para crear un intent.
        // Extraemos el secret client key y del PaymentIntent creado y lo retornamos.
        return "";
    }

    public void handleWebhook(String payload, String signature) {
        // Comprbar Firma signature
        // Obtener orderId de los metadatos, supongo que estan en el payload
        // Buscar el order por orderId
            // Si no existe return.
        // Si existe entonces extraemos el evento
        // Comprobamos el estado del evento
        // Si es Existoso
            // CAmbiamos estado de order a PAID
            // retornamos 200 ok
        // Sino lo dejamos como esta
        // retornamos codigo de error.
    }
}
