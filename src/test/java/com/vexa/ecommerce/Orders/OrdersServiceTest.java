package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Cart.Cart;
import com.vexa.ecommerce.Cart.CartItems;
import com.vexa.ecommerce.Cart.CartService;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;
import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Users.Role;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    CartService cartService;
    @Mock
    UsersService usersService;
    @Mock
    OrdersRepository ordersRepository;
    @InjectMocks
    OrdersService ordersService;

    private Orders createOrder(Integer id) {
        Orders o = new Orders(OrdersStatus.PENDING, 10.0, "shippingAddress", LocalDateTime.now(), LocalDateTime.now(), "paymentIntentId", LocalDateTime.now());
        o.setOrderId(id);
        return o;
    }

    @Test
    void createOrderFromCart() {
        // Preparación de datos
        Users user = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user.setUserId(1);
        Users user2 = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user2.setUserId(2);

        Products product = new Products("name", 10.0, "description", "url_image", 10);
        product.setProductId(1);

        Cart cart = new Cart(1, user);
        CartItems cartItem = new CartItems(cart, product, 10);
        cart.getCartItemsList().add(cartItem);
        Cart emptyCart = new Cart(2, user2);

        Orders order = createOrder(1);
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setShippingAddress("shippingAddress");

        // Ejecución de lógica
        when(usersService.getUserById(user.getUserId())).thenReturn(user);
        when(cartService.getCartByUserId(user.getUserId())).thenReturn(cart);
        when(ordersRepository.save(any(Orders.class)))
                .thenAnswer(invocation -> {
                    Orders o = invocation.getArgument(0);
                    o.setOrderId(1);
                    return o;
                });
        Orders createdOrder = ordersService.createOrderFromCart(user.getUserId(), dto);

        // Comprobaciones del resultado
        assertNotNull(createdOrder);
        assertEquals("shippingAddress", createdOrder.getShippingAddress());
        assertEquals(OrdersStatus.PENDING, createdOrder.getStatus());
        assertEquals(100, createdOrder.getTotalPrice());
        assertEquals(1, createdOrder.getOrderItemsList().size());
        assertEquals(0, product.getStock());
        verify(cartService, times(1)).clearCart(user.getUserId());
    }

    @Test
    void saveOrder_ShouldSaveOrder() {
        // Preparación de datos
        Orders order = createOrder(1);

        // Ejecución de lógica
        when(ordersRepository.save(order)).thenReturn(order);
        Orders savedOrder = ordersService.saveOrder(order);

        // Comprobaciones del resultado
        assertNotNull(savedOrder);
        assertEquals(order.getOrderId(), savedOrder.getOrderId());
    }

    @Test
    void getOrderByOrderId_shouldReturnProduct_whenOrderExists() {
        // Preparación de datos
        Orders order = createOrder(1);
        Optional<Orders> ordersOptional = Optional.of(order);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(ordersOptional);
        Orders obtainedOrder = ordersService.getOrderByOrderId(order.getOrderId());

        // Comprobaciones del resultado
        assertNotNull(ordersOptional);
        assertEquals(ordersOptional.get().getOrderId(), obtainedOrder.getOrderId());
    }

    @Test
    void getOrderByOrderId_shouldThrowException_whenOrderDoesNotExist() {
        // Preparación de datos
        Orders order = createOrder(1);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException =  assertThrows(ResourceNotFoundException.class, () -> {
            ordersService.getOrderByOrderId(order.getOrderId());
        });

        // Comprobaciones del resultado
        assertEquals("Order with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void getOrdersByUserId_shouldReturnOrders() {
        // Preparación de datos
        Orders order = createOrder(1);
        Orders order2 = createOrder(2);
        Optional<List<Orders>> ordersOptionalList = Optional.of(List.of(order, order2));

        // Ejecución de lógica
        when(ordersRepository.findByUser_UserId(order.getOrderId())).thenReturn(ordersOptionalList);
        List<Orders> ordersList = ordersService.getOrdersByUserId(order.getOrderId());

        // Comprobaciones del resultado
        assertEquals(ordersOptionalList.get().size(), ordersList.size());
        assertEquals(ordersOptionalList.get().get(0).getOrderId(), ordersList.get(0).getOrderId());
    }

    @Test
    void updateOrder_shouldUpdateSuccessfully() {
        // Preparación de datos
        Orders order = createOrder(1);
        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO(OrdersStatus.PAID, "shippingAdressUpdated", "paymentIntentId", LocalDateTime.now());
        Optional<Orders> ordersOptional = Optional.of(order);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(ordersOptional);
        when(ordersRepository.existsByPaymentIntentId(dto.paymentIntentId())).thenReturn(false);
        when(ordersRepository.save(order)).thenReturn(order);
        Orders updatedOrder = ordersService.updateOrder(order.getOrderId(), dto);

        //Comprobaciones de resultado
        assertNotNull(updatedOrder);
        assertEquals(order.getOrderId(), updatedOrder.getOrderId());
        assertEquals("shippingAdressUpdated", updatedOrder.getShippingAddress());
    }

    @Test
    void updateOrder_shouldThrowException_whenUserNotFound() {
        // Preparación de datos
        Orders order = createOrder(1);
        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO(OrdersStatus.PAID, "shippingAdressUpdated", "paymentIntentId", LocalDateTime.now());

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ordersService.updateOrder(order.getOrderId(), dto);
        });

        //Comprobaciones de resultado
        Assertions.assertEquals("Order with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateOrder_shouldThrowException_whenPaymentIntentIdAlreadyExists() {
        // Preparación de datos
        Orders order = createOrder(1);
        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO(OrdersStatus.PAID, "shippingAdressUpdated", "paymentIntentId", LocalDateTime.now());
        Optional<Orders> ordersOptional = Optional.of(order);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(ordersOptional);
        when(ordersRepository.existsByPaymentIntentId(dto.paymentIntentId())).thenReturn(true);
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
            ordersService.updateOrder(order.getOrderId(), dto);
        });

        //Comprobaciones de resultado
        Assertions.assertEquals("PaymentIntentId is already in use", badRequestException.getMessage());

    }

    @Test
    void cancelOrderByUser_shouldCancelSuccessfully() {
        // Preparación de datos
        Users user = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user.setUserId(1);
        Orders order = createOrder(1);
        order.setUser(user);
        Optional<Orders> ordersOptional = Optional.of(order);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(ordersOptional);
        when(ordersRepository.save(order)).thenReturn(order);
        Orders canceledOrder = ordersService.cancelOrderByUser(order.getOrderId(), user.getUserId());

        // Comprobaciones del resultado
        assertEquals(OrdersStatus.CANCELLED, canceledOrder.getStatus());
    }

    @Test
    void cancelOrderByUser_shouldThrowException_whenOrderNotFound() {
        // Preparación de datos
        Users user = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user.setUserId(1);
        Orders order = createOrder(1);
        order.setUser(user);

        // Ejecución de lógica
        when(ordersRepository.findById(order.getOrderId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ordersService.cancelOrderByUser(order.getOrderId(), user.getUserId());
        });

        // Comprobaciones del resultado
        Assertions.assertEquals("Order with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void getOrderByPaymentIntentId_shouldReturnSuccessfully() {
        // Preparación de datos
        String paymentIntentId = "paymentIntentId";
        Orders order = createOrder(1);
        Optional<Orders> ordersOptional = Optional.of(order);

        // Ejecución de lógica
        when(ordersRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(ordersOptional);
        Orders obtainedOrder = ordersService.getOrderByPaymentIntentId(paymentIntentId);

        // Comprobaciones de lógica
        assertNotNull(obtainedOrder);
        assertEquals(paymentIntentId, obtainedOrder.getPaymentIntentId());
    }

    @Test
    void getOrderByPaymentIntentId_shouldThrowException_whenOrderNotFoundWithPaymentIntentId() {
        // Preparación de datos
        String paymentIntentId = "paymentIntentId";

        // Ejecución de lógica
        when(ordersRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ordersService.getOrderByPaymentIntentId(paymentIntentId);
        });

        // Comprobaciones de lógica
        // TODO: Hay que revisar como retornar el error mejor, no tiene lógica el mensaje
        Assertions.assertEquals("Order payment Intent id: paym...ntId with id null not found", resourceNotFoundException.getMessage());

    }
}