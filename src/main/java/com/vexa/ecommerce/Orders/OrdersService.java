package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Cart.Cart;
import com.vexa.ecommerce.Cart.CartItems;
import com.vexa.ecommerce.Cart.CartService;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;
import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vexa.ecommerce.Utils.SecurityUtils.maskKey;

@Service
@Slf4j
public class OrdersService implements IOrdersService {

    private final OrdersRepository ordersRepository;
    private final CartService cartService;
    private final UsersService usersService;

    public OrdersService(OrdersRepository ordersRepository, CartService cartService, UsersService usersService) {
        this.ordersRepository = ordersRepository;
        this.cartService = cartService;
        this.usersService = usersService;
    }

    @Override
    @Transactional
    public Orders createOrderFromCart(Integer userId, OrdersRequestDTO dto) {
        // Obtener el User
        Users user = usersService.getUserById(userId);

        // Obtener el Cart y comprobar que no esté vacío
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getCartItemsList().isEmpty()) {
            log.warn("Cart from user with ID {} is empty. Cannot create an order", userId);
            throw new BadRequestException("Your cart is empty, cannot create an order.");
        }

        // Crear Order
        Orders order = new Orders();
        order.setStatus(OrdersStatus.PENDING);
        order.setShippingAddress(dto.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUser(user);

        // Guardar el order
        Orders savedOrder = ordersRepository.save(order);

        // Función para agregar cartItems a orderItems
        addCartItemsToOrderItems(cart.getCartItemsList(), savedOrder);

        double totalPrice = cart.getCartItemsList()
                .stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        savedOrder.setTotalPrice(totalPrice);

        // Vaciar carrito
        cartService.clearCart(userId);

        log.info("Order with ID {} has been created for user with ID {}", savedOrder.getOrderId(), userId);
        return savedOrder;
    }

    private void addCartItemsToOrderItems(List<CartItems> cartItemsList, Orders savedOrder) {
        for (CartItems ci : cartItemsList) {
            Products product = ci.getProduct();
            int qty = ci.getQuantity();

            if (product.getStock() < qty) {
                log.warn("Not enough stock for product with ID {}. Product stock: {} requested stock: {}. Cannot create an order", product.getProductId(), product.getStock(), qty);
                throw new BadRequestException("Not enough stock for " + product.getName());
            }

            OrderItems oi = new OrderItems();
            oi.setOrder(savedOrder);
            oi.setProduct(product);
            oi.setQuantity(qty);
            oi.setPriceAtPurchase(product.getPrice());

            // ID compuesta
            oi.setId(new OrderItemsId(
                    product.getProductId(),
                    savedOrder.getOrderId()
            ));

            savedOrder.getOrderItemsList().add(oi);

            // Reducir stock
            product.setStock(product.getStock() - qty);
        }
    }

    public Orders saveOrder(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public Orders getOrderByOrderId(Integer orderId) {
        return ordersRepository.findById(orderId).orElseThrow(() -> {
            log.warn("Order id {} not found. The order could not be obtained", orderId);
            return new ResourceNotFoundException("Order", orderId);
        });
    }

    @Override
    public List<Orders> getOrdersByUserId(Integer userId) {
        Optional<List<Orders>> optionalListOrders = ordersRepository.findByUser_UserId(userId);

        if (optionalListOrders.isPresent()) {
            log.info("Orders has been obtained by user ID {}", userId);
            return optionalListOrders.get();
        }
        return new ArrayList<Orders>();
    }

    @Override
    public Orders updateOrder(Integer id, UpdateOrderRequestDTO dto) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} not found. The order could not be updated", id);
                    return new ResourceNotFoundException("Order", id);
                });

        if (dto.status() != null) {
            OrdersStatus current = order.getStatus();
            OrdersStatus next = dto.status();

            if (!current.canTransitionTo(next)) {
                log.warn("Invalid status transition from {} to {} in order with ID {}. The order could not be updated", current, next, id);
                throw new BadRequestException(
                        "Invalid status transition from " + current + " to " + next
                );
            }

            order.setStatus(next);
        }

        if (dto.shippingAddress() != null) {
            order.setShippingAddress(dto.shippingAddress());
        }

        if (dto.paymentIntentId() != null) {
            if (ordersRepository.existsByPaymentIntentId(dto.paymentIntentId())) {
                log.warn("PaymentIntentId is already in use. The order with ID {} could not be updated", id);
                throw new BadRequestException("PaymentIntentId is already in use");
            }
            order.setPaymentIntentId(dto.paymentIntentId());
        }

        if (dto.paidAt() != null) {
            order.setPaidAt(dto.paidAt());
        }

        log.info("Order with id {} has been updated", id);
        return this.ordersRepository.save(order);
    }

    public Orders cancelOrderByUser(Integer orderId, Integer userId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} not found. The order could not be canceled", orderId);
                    return new ResourceNotFoundException("Order", orderId);
                });

        // Validar propiedad
        if (!order.getUser().getUserId().equals(userId)) {
            log.warn("Order with ID {} does not belong to the user with ID {}. The order could not be canceled", orderId, userId);
            throw new BadRequestException("You are not allowed to cancel this order.");
        }

        // Validar estado
        if (order.getStatus() != OrdersStatus.PENDING) {
            log.warn("Only PENDING orders can be cancelled. The order with ID {} and status {} could not be canceled", orderId, order.getStatus());
            throw new BadRequestException("Only PENDING orders can be cancelled.");
        }

        order.setStatus(OrdersStatus.CANCELLED);
        log.info("Order with id {} has been canceled", orderId);
        return ordersRepository.save(order);
    }


    public Orders getOrderByPaymentIntentId(String paymentIntentId) {
        Optional<Orders> optionalOrder = ordersRepository.findByPaymentIntentId(paymentIntentId);

        if (optionalOrder.isPresent()) {
            log.info("Order with id {} has been obtained by payment intent id", optionalOrder.get().getOrderId());
            return optionalOrder.get();
        }

        log.info("Order with paymentIntentId start with {} not found. Order cannot be obtained", maskKey(paymentIntentId));
        throw new ResourceNotFoundException("Order payment Intent id: " + maskKey(paymentIntentId), null);
    }
}
