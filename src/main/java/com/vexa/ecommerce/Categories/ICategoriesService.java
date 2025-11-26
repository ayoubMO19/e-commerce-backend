package com.vexa.ecommerce.Categories;

import java.util.List;

public interface ICategoriesService {

    List<Categories> getAllCategories();

    Categories getCategoryById(Integer id);

    Categories saveNewCategory(Categories category);

    Categories updateCategory(Categories category);

    void deleteCategoryById(Integer id);
}
