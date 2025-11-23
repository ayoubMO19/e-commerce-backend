package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    private String status;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // Empty Constructor
    public Orders() {}

    // Constructor
    public Orders(String status, String shippingAddress, Date createdAt, Date updatedAt) {
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
