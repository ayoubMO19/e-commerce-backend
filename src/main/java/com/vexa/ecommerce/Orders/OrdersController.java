package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrderResponseDTO;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;
import com.vexa.ecommerce.Security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<OrderResponseDTO> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrdersRequestDTO dto
    ) {
        Orders order = ordersService.createOrderFromCart(userDetails.getUserId(), dto);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    // Obtener órdenes del usuario
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Orders> ordersList = ordersService.getOrdersByUserId(userDetails.getUserId());
        List<OrderResponseDTO> orderResponseDTOList = ordersList.stream()
                .map(OrderMapper::toDTO)
                .toList();

        return ResponseEntity.ok(orderResponseDTOList);
    }

    // TODO: Obtener los orders de un user específico (SOLO PARA ADMINS)

    // Actualizar el status de un order
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderRequestDTO dto
    ) {
        Orders order = ordersService.updateOrder(id, dto);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelMyOrder(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Orders order = ordersService.cancelOrderByUser(id, userDetails.getUserId());
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }


}

