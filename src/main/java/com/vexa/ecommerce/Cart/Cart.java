package com.vexa.ecommerce.Cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItems> cartItemsList;

    // Empty Constructor
    public Cart() {}

    // Constructor
    public Cart(Integer cartId, Users user) {
        this.cartId = cartId;
        this.user = user;
    }


}
