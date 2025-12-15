package com.vexa.ecommerce.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Cart.Cart;
import com.vexa.ecommerce.Comments.Comments;

import com.vexa.ecommerce.Orders.Orders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users implements UserDetails {
    // Variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "has_welcome_discount")
    private Boolean hasWelcomeDiscount = false;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

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
    public Users(String name, String surname, String email, Boolean hasWelcomeDiscount, String password, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.hasWelcomeDiscount = hasWelcomeDiscount;
        this.password = password;
        this.role = role;
        this.enabled = false;
    }

    // constructor SIN role (para compatibilidad):
    public Users(String name, String surname, String email, Boolean hasWelcomeDiscount, String password) {
        this(name, surname, email, hasWelcomeDiscount, password, Role.USER);
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id=" + userId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", hasWelcomeDiscount=" + hasWelcomeDiscount +
                ", role=" + role +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
