package com.vexa.ecommerce.Cart;

import com.vexa.ecommerce.Products.Products;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class CartItems {

    @EmbeddedId
    private CartItemsId id;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Products product;

    private Integer quantity;

    public CartItems() {}

    public CartItems(Cart cart, Products product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.id = new CartItemsId(cart.getCartId(), product.getProductId());
    }

    @Override
    public String toString() {
        return "CartItems{" +
                "id=" + id +
                ", cart=" + cart +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
