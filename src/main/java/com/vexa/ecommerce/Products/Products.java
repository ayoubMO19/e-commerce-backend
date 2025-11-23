package com.vexa.ecommerce.Products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Cart.CartItems;
import com.vexa.ecommerce.Categories.Categories;
import com.vexa.ecommerce.Comments.Comments;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    private String name;
    private Double price;
    private String description;

    @Column(name = "url_image")
    private String urlImage;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Comments> commentsList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItems> cartItemsList;

    // Empty Constructor
    public Products() {}

    // Constructor
    public Products(String name, Double price, String description, String urlImage, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.urlImage = urlImage;
        this.stock = stock;
    }


    @Override
    public String toString() {
        return "Products{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", stock=" + stock +
                '}';
    }
}
