package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Cart.DTOs.CartAddRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartUpdateRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Integer userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // POST /cart/add
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartAddRequestDTO dto) {
        Cart cart = cartService.addProductToCart(
                dto.getUserId(),
                dto.getProductId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // PUT /cart/update
    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateQuantity(@RequestBody CartUpdateRequestDTO dto) {
        Cart cart = cartService.updateQuantity(
                dto.getUserId(),
                dto.getProductId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // DELETE /cart/{userId}/product/{productId}
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<CartResponseDTO> removeProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId
    ) {
        Cart cart = cartService.removeProduct(userId, productId);
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }
}
