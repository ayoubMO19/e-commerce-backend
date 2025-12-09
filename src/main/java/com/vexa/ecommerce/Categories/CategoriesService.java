package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriesService implements ICategoriesService {

    private final CategoriesRepository categoriesRepository;

    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<Categories> getAllCategories() {
        return this.categoriesRepository.findAll();
    }

    @Override
    public Categories getCategoryById(Integer id) {
        return this.categoriesRepository.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException("Category", id);
        });
    }

    @Override
    public Categories saveNewCategory(Categories category) {
        if (categoriesRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists.");
        }

        return this.categoriesRepository.save(category);
    }

    @Override
    public Categories updateCategory(Categories category) {
        this.categoriesRepository.findById(category.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", category.getCategoryId()));

        return this.categoriesRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Integer id) {
        if(!this.categoriesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        this.categoriesRepository.deleteById(id);
    }
}
