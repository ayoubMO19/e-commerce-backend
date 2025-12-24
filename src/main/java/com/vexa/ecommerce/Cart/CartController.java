package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Cart.DTOs.CartAddRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartDeleteProductRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartUpdateRequestDTO;
import com.vexa.ecommerce.Cart.DTOs.CartResponseDTO;
import com.vexa.ecommerce.Products.DTOs.ProductResponseDTO;
import com.vexa.ecommerce.Security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Cart", description = "Endpoints para gestionar el carrito")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart/
    @Operation(
            summary = "Obtener carrito del user logueado",
            description = "Obtener carrito del user logueado",
            tags = {"Cart"}
    )
    @ApiResponse(responseCode = "200", description = "Cart obtenido existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))) // Respuesta exitosa
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.getCartByUserId(userDetails.getUserId());
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }

    // POST /cart/add
    @Operation(
            summary = "Agregar producto al carrito del user logueado",
            description = "Agregar producto al carrito del user logueado",
            tags = {"Cart"}
    )
    @ApiResponse(responseCode = "200", description = "Product agregado al Cart existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))) // Respuesta exitosa
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
    @Operation(
            summary = "Actualizar cantidad de producto del carrito del user logueado",
            description = "Actualizar cantidad de producto del carrito del user loguead",
            tags = {"Cart"}
    )
    @ApiResponse(responseCode = "200", description = "Cantidad de Product actualizado en Cart existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))) // Respuesta exitosa
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
    @Operation(
            summary = "Eliminar producto del carrito del user logueado",
            description = "Eliminar producto del carrito del user logueado",
            tags = {"Cart"}
    )
    @ApiResponse(responseCode = "200", description = "Product eliminado de Cart existosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))) // Respuesta exitosa
    @DeleteMapping("/delete")
    public ResponseEntity<CartResponseDTO> removeProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartDeleteProductRequestDTO dto
    ) {
        Cart cart = cartService.removeProduct(userDetails.getUserId(), dto.getProductId());
        return ResponseEntity.ok(cartService.convertToDTO(cart));
    }
}
