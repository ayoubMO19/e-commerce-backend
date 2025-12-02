package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Cart.Cart;
import com.vexa.ecommerce.Cart.CartItems;
import com.vexa.ecommerce.Cart.CartService;
import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Products.ProductsService;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import com.vexa.ecommerce.exceptions.BadRequestException;
import com.vexa.ecommerce.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService implements IOrdersService {

    private final OrdersRepository ordersRepository;
    private final CartService cartService;
    private final UsersService usersService;
    private final ProductsService productsService;

    public OrdersService(OrdersRepository ordersRepository, CartService cartService, UsersService usersService, ProductsService productsService) {
        this.ordersRepository = ordersRepository;
        this.cartService = cartService;
        this.usersService = usersService;
        this.productsService = productsService;
    }

    @Override
    @Transactional
    public Orders createOrderFromCart(Integer userId, OrdersRequestDTO dto) {
        // Obtener el Cart y el User
        Cart cart = cartService.getCartByUserId(userId);
        Users user = usersService.getUserById(userId);

        if (cart.getCartItemsList() == null || cart.getCartItemsList().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Crear Order
        Orders order = new Orders();
        order.setStatus("pending");
        order.setShippingAddress(dto.getShippingAddress());
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        order.setUser(user);

        // primero guardas el order
        Orders savedOrder = ordersRepository.save(order);

        for (CartItems ci : cart.getCartItemsList()) {
            Products product = ci.getProduct();
            int qty = ci.getQuantity();

            if (product.getStock() < qty) {
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
            productsService.updateProduct(product);
        }

        double totalPrice = cart.getCartItemsList()
                .stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        savedOrder.setTotalPrice(totalPrice);

        // Guardar savedOrder (cascade guardará OrderItems)
        Orders saved = ordersRepository.save(savedOrder);

        // Vaciar carrito
        cartService.clearCart(userId);

        return saved;
    }

    @Override
    public List<Orders> getOrdersByUserId(Integer userId) {
        Optional<List<Orders>> optionalListOrders = ordersRepository.findByUser_UserId(userId);

        if (optionalListOrders.isPresent()) {
            return optionalListOrders.get();
        }

        throw new ResourceNotFoundException("Orders for user", userId);
    }
}
