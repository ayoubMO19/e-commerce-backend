package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // Crear una orden desde el carrito
    @PostMapping
    public Orders createOrder(@Valid @RequestBody OrdersRequestDTO dto) {
        return ordersService.createOrderFromCart(dto.getUserId(), dto);
    }

    // Obtener órdenes por usuario
    @GetMapping
    public List<Orders> getOrders(@RequestParam Integer userId) {
        return ordersService.getOrdersByUserId(userId);
    }
}

