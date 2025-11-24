package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Cart.CartItemsId;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class OrderItemsId implements Serializable {
    private Integer productId;
    private Integer orderId;

    public OrderItemsId() {}

    public OrderItemsId(Integer productId, Integer orderId) {
        this.productId = productId;
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemsId that)) return false;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}
