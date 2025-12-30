package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;
import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Products.ProductsService;
import com.vexa.ecommerce.Users.Users;
import com.vexa.ecommerce.Users.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    UsersService usersService;

    @Mock
    ProductsService productsService;

    @InjectMocks
    CartService cartService;

    Users user;
    Products product;
    Cart cart;

    @BeforeEach
    void setup() {
        user = new Users();
        user.setUserId(1);

        product = new Products();
        product.setProductId(10);
        product.setName("Product");
        product.setPrice(5.0);
        product.setStock(10);

        cart = new Cart();
        cart.setUser(user);
        cart.setCartItemsList(new ArrayList<>());
    }

    @Test
    void getCartByUserId_shouldReturnExistingCart() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);

        Cart result = cartService.getCartByUserId(1);

        assertEquals(cart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartByUserId_shouldCreateCartIfNotExists() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.empty());
        when(usersService.getUserById(1)).thenReturn(user);
        when(cartRepository.save(any())).thenReturn(cart);

        Cart result = cartService.getCartByUserId(1);

        assertEquals(user, result.getUser());
        verify(cartRepository).save(any());
    }

    @Test
    void addProductToCart_shouldAddNewItem() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);
        when(productsService.getProductById(10)).thenReturn(product);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.addProductToCart(1, 10, 2);

        assertEquals(1, result.getCartItemsList().size());
        assertEquals(2, result.getCartItemsList().get(0).getQuantity());
    }

    @Test
    void addProductToCart_shouldThrowIfNoStock() {
        product.setStock(1);

        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);
        when(productsService.getProductById(10)).thenReturn(product);

        assertThrows(
                BadRequestException.class,
                () -> cartService.addProductToCart(1, 10, 5)
        );
    }

    @Test
    void updateQuantity_shouldUpdateSuccessfully() {
        CartItems item = new CartItems(cart, product, 1);
        cart.getCartItemsList().add(item);

        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.updateQuantity(1, 10, 3);

        assertEquals(3, result.getCartItemsList().get(0).getQuantity());
    }

    @Test
    void updateQuantity_shouldRemoveIfZero() {
        CartItems item = new CartItems(cart, product, 1);
        cart.getCartItemsList().add(item);

        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.updateQuantity(1, 10, 0);

        assertTrue(result.getCartItemsList().isEmpty());
    }

    @Test
    void updateQuantity_shouldThrowIfItemNotFound() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateQuantity(1, 10, 1)
        );
    }

    @Test
    void removeProduct_shouldRemoveItem() {
        CartItems item = new CartItems(cart, product, 1);
        cart.getCartItemsList().add(item);

        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.removeProduct(1, 10);

        assertTrue(result.getCartItemsList().isEmpty());
    }

    @Test
    void clearCart_shouldEmptyCart() {
        cart.getCartItemsList().add(new CartItems(cart, product, 1));

        when(cartRepository.findByUser_UserId(1)).thenReturn(Optional.of(cart));
        when(usersService.getUserById(1)).thenReturn(user);

        cartService.clearCart(1);

        assertTrue(cart.getCartItemsList().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void convertToDTO_shouldMapCorrectly() {
        cart.getCartItemsList().add(new CartItems(cart, product, 2));

        CartResponseDTO dto = cartService.convertToDTO(cart);

        assertEquals(1, dto.getItems().size());
        assertEquals(10, dto.getItems().get(0).getProductId());
        assertEquals(2, dto.getItems().get(0).getQuantity());
    }
}
