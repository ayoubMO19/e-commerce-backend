package com.vexa.ecommerce.Orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Enumerated(EnumType.STRING)
    private OrdersStatus status;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<OrderItems> orderItemsList;

    // Empty Constructor
    public Orders() {
        this.orderItemsList = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // Constructor
    public Orders(OrdersStatus status, Double totalPrice, String shippingAddress, Date createdAt, Date updatedAt) {
        this.status = status;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItemsList = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
