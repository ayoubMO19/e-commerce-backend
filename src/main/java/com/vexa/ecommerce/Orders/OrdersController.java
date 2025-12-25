package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Auth.DTOs.AuthResponseDTO;
import com.vexa.ecommerce.Orders.DTOs.OrderResponseDTO;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;
import com.vexa.ecommerce.Security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "Bearer Authentication")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // Actualizar el status de un order
    @Operation(
            summary = "(ADMIN) - Actualizar el status de una Order específica",
            description = "Actualizar el status de uan Order específica",
            tags = {"Admin"}
    )
    @ApiResponse(responseCode = "200", description = "Order status actualizado existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))) // Respuesta exitosa
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderRequestDTO dto
    ) {
        Orders order = ordersService.updateOrder(id, dto);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    // Crear una orden desde el carrito
    @Operation(
            summary = "Crear order para User logueado",
            description = "Crear order para el user logueado usando su carrito",
            tags = {"Orders"}
    )
    @ApiResponse(responseCode = "200", description = "Order creada existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))) // Respuesta exitosa
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrdersRequestDTO dto
    ) {
        Orders order = ordersService.createOrderFromCart(userDetails.getUserId(), dto);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    // Obtener órdenes del usuario
    @Operation(
            summary = "Obtener orders del User logueado",
            description = "Obtener todas las orders del user logueado",
            tags = {"Orders"}
    )
    @ApiResponse(responseCode = "200", description = "Orders obtenidas existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))) // Respuesta exitosa
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Orders> ordersList = ordersService.getOrdersByUserId(userDetails.getUserId());
        List<OrderResponseDTO> orderResponseDTOList = ordersList.stream()
                .map(OrderMapper::toDTO)
                .toList();

        return ResponseEntity.ok(orderResponseDTOList);
    }

    // TODO: Obtener los orders de un user específico (SOLO PARA ADMINS)

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancelar una Order específica",
            description = "Cancelar una Order específica actualizando su status a canceled",
            tags = {"Orders"}
    )
    @ApiResponse(responseCode = "200", description = "Order cancelada existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))) // Respuesta exitosa
    public ResponseEntity<OrderResponseDTO> cancelMyOrder(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Orders order = ordersService.cancelOrderByUser(id, userDetails.getUserId());
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }


}

