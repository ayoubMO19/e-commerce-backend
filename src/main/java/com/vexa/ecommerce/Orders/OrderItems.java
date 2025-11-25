package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Products.Products;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class OrderItems {
    @EmbeddedId
    private OrderItemsId id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Products product;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Orders order;

    private Integer quantity;
    @Column(name = "price_at_purchase")
    private Double priceAtPurchase;

    public OrderItems() {}

    public OrderItems(Products product, Orders order, Integer quantity, Double priceAtPurchase) {
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.id = new OrderItemsId(product.getProductId(), order.getOrderId());
    }

    @Override
    public String toString() {
        return "OrderItems{" +
                "id=" + id +
                ", product=" + product +
                ", order=" + order +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                '}';
    }
}
