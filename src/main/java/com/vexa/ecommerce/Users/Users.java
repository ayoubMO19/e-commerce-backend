package com.vexa.ecommerce.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Cart.Cart;
import com.vexa.ecommerce.Comments.Comments;

import com.vexa.ecommerce.Orders.Orders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users {
    // Variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    private String name;
    private String surname;
    private String email;

    @Column(name = "has_welcome_discount")
    private Boolean hasWelcomeDiscount;

    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comments> commentsList;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Orders> ordersList;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    // Empty Constructor
    public Users() {}

    // Constructor
    public Users(String name, String surname, String email, Boolean hasWelcomeDiscount, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.hasWelcomeDiscount = hasWelcomeDiscount;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id=" + userId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", hasWelcomeDiscount=" + hasWelcomeDiscount +
                ", password='" + password + '\'' +
                '}';
    }
}
