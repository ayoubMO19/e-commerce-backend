package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrderResponseDTO;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrdersRequestDTO dto) {
        Orders order = ordersService.createOrderFromCart(dto.getUserId(), dto);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    // Obtener órdenes por usuario
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@RequestParam Integer userId) {
        List<Orders> ordersList = ordersService.getOrdersByUserId(userId);
        List<OrderResponseDTO> orderResponseDTOList = ordersList.stream()
                .map(OrderMapper::toDTO)
                .toList();

        return ResponseEntity.ok(orderResponseDTOList);
    }
}

