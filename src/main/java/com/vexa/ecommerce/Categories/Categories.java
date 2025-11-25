package com.vexa.ecommerce.Categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vexa.ecommerce.Products.Products;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name="categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Products> productsList;

    // Empty Constructor
    public Categories(){}

    // Constructor
    public Categories(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}
