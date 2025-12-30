package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Products.ProductsService;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vexa.ecommerce.Cart.DTOs.CartItemDTO;
import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final UsersService usersService;
    private final ProductsService productsService;

    public CartService(CartRepository cartRepository, UsersService usersService, ProductsService productsService) {
        this.cartRepository = cartRepository;
        this.usersService = usersService;
        this.productsService = productsService;
    }

    public Cart getCartByUserId(Integer userId) {
        Optional<Cart> cartOptional = cartRepository.findByUser_UserId(userId);
        Users user = usersService.getUserById(userId);

        if (cartOptional.isPresent()) {
            return cartOptional.get();
        }

        Cart newCart = new Cart();
        newCart.setUser(user);
        log.info("Cart for user with ID {} has been created and obtained", userId);
        return cartRepository.save(newCart);
    }

    @Transactional
    public Cart addProductToCart(Integer userId, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn("Quantity must be greater than zero. Quantity: {}. Cannot add product with ID {} to the cart of the user with ID {}", quantity, productId, userId);
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Cart cart = getCartByUserId(userId);
        Products product = productsService.getProductById(productId);

        // Validar stock
        if (product.getStock() < quantity) {
            log.warn("Not enough stock for product with ID {}. Product stock: {} requested stock: {}. Cannot add producto to Cart with ID: {}", product.getProductId(), product.getStock(), quantity, cart.getCartId());
            throw new BadRequestException("Not enough stock for product " + product.getName());
        }

        // Buscar si ya existe en el carrito
        Optional<CartItems> existingItem = cart.getCartItemsList().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;

            // Validar stock para suma
            if (product.getStock() < newQuantity) {
                log.warn("Stock exceeded for product with ID {}. Product stock: {} is less than the new stock: {}. Cannot add producto to Cart with ID: {}", product.getProductId(), product.getStock(), newQuantity, cart.getCartId());
                throw new BadRequestException("Stock exceeded for " + product.getName());
            }

            existingItem.get().setQuantity(newQuantity);
        } else {
            CartItems newItem = new CartItems(cart, product, quantity);
            cart.getCartItemsList().add(newItem);
        }

        log.info("Product with ID {} has been added to the cart for user with ID {}", productId, userId);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Integer userId, Integer productId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            log.warn("Quantity must be greater than zero. Quantity: {}. Cannot update quantity product with ID {} to the cart of the user with ID {}", quantity, productId, userId);
            throw new BadRequestException("Quantity cannot be negative");
        }

        Cart cart = getCartByUserId(userId);
        CartItems item = findCartItem(cart, productId);

        // Si la cantidad es 0 elimina el producto de la lista
        if (quantity == 0) {
            cart.getCartItemsList().remove(item);
            log.info("Update Quantity to 0. Product with ID {} has been removed in the cart for user with ID {}", productId, userId);
            return cartRepository.save(cart);
        }

        // Validar stock
        if (item.getProduct().getStock() < quantity) {
            log.warn("Insufficient quantity to update the quantity of product with id {} in the cart of user with id {}. Product stock: {} Request quantity: {} ", productId, userId, item.getProduct().getStock(), quantity);
            throw new BadRequestException("Not enough stock");
        }

        item.setQuantity(quantity);
        log.info("Quantity product with ID {} has been updated in the cart for user with ID {} to quantity {}", productId, userId, quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProduct(Integer userId, Integer productId) {
        Cart cart = getCartByUserId(userId);

        boolean removed = cart.getCartItemsList().removeIf(
                item -> item.getProduct().getProductId().equals(productId)
        );
        if (!removed){
            log.warn("Product with ID {} not exist in the cart for user with ID {}", productId, userId);
        }
        else {
            log.info("Product with ID {} has been removed in the cart for user with ID {}", productId, userId);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItemsList().clear();
        log.info("Cart has been cleaned for user with ID {}", userId);
        cartRepository.save(cart);
    }

    public CartResponseDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getCartItemsList().stream()
                .map(ci -> new CartItemDTO(
                        ci.getProduct().getProductId(),
                        ci.getProduct().getName(),
                        ci.getProduct().getPrice(),
                        ci.getQuantity()
                ))
                .toList();

        return new CartResponseDTO(cart.getUser().getUserId(), items);
    }

    private CartItems findCartItem(Cart cart, Integer productId) {
        return cart.getCartItemsList().stream()
                .filter(i -> i.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", productId));
    }


}
