package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Cart.DTOs.CartAddRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartDeleteProductRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartUpdateRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;
import com.vexa.ecommerce.Security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart/
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.getCartByUserId(userDetails.getUserId());
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // POST /cart/add
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartAddRequestDTO dto) {
        Cart cart = cartService.addProductToCart(
                userDetails.getUserId(),
                dto.getProductId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // PUT /cart/update
    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartUpdateRequestDTO dto) {
        Cart cart = cartService.updateQuantity(
                userDetails.getUserId(),
                dto.getProductId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // DELETE /cart/delete
    @DeleteMapping("/delete")
    public ResponseEntity<CartResponseDTO> removeProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartDeleteProductRequestDTO dto
    ) {
        Cart cart = cartService.removeProduct(userDetails.getUserId(), dto.getProductId());
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }
}
