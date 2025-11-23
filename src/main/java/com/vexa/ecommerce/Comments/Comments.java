package com.vexa.ecommerce.Comments;

import com.vexa.ecommerce.Products.Products;
import com.vexa.ecommerce.Users.Users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="comments")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    private String description;
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    // Empty Constructor
    public Comments() {}

    // Constructor
    public Comments(String description, Integer rating) {
        this.description = description;
        this.rating = rating;
    }


    @Override
    public String toString() {
        return "Comments{" +
                "commentId=" + commentId +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}
