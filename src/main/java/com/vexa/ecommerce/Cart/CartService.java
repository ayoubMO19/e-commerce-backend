package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Products.ProductsService;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.vexa.ecommerce.Cart.DTOs.CartItemDTO;
import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
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
        return cartRepository.save(newCart);
    }

    @Transactional
    public Cart addProductToCart(Integer userId, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Cart cart = getCartByUserId(userId);
        Products product = productsService.getProductById(productId);

        // Validar stock
        if (product.getStock() < quantity) {
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
                throw new BadRequestException("Stock exceeded for " + product.getName());
            }

            existingItem.get().setQuantity(newQuantity);
        } else {
            CartItems newItem = new CartItems(cart, product, quantity);
            cart.getCartItemsList().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Integer userId, Integer productId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }

        Cart cart = getCartByUserId(userId);
        CartItems item = findCartItem(cart, productId);

        // Si la cantidad es 0 elimina el producto de la lista
        if (quantity == 0) {
            cart.getCartItemsList().remove(item);
            return cartRepository.save(cart);
        }

        // Validar stock
        if (item.getProduct().getStock() < quantity) {
            throw new BadRequestException("Not enough stock");
        }

        item.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProduct(Integer userId, Integer productId) {
        Cart cart = getCartByUserId(userId);

        cart.getCartItemsList().removeIf(
                item -> item.getProduct().getProductId().equals(productId)
        );

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(Integer userId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItemsList().clear();
        return cartRepository.save(cart);
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
