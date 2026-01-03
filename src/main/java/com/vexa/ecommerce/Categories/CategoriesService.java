package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
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
        return this.categoriesRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Category with id {} not found", id);
                return new ResourceNotFoundException("Category", id);
            });
    }

    @Override
    public Categories saveNewCategory(Categories category) {
        if (categoriesRepository.existsByName(category.getName())) {
            log.warn("Category name {} already exists. The category could not be saved", category.getName());
            throw new BadRequestException("Category name already exists.");
        }

        log.info("Category with name {} has been created", category.getName());
        return this.categoriesRepository.save(category);
    }

    @Override
    public Categories updateCategory(Categories category) {
        this.categoriesRepository.findById(category.getCategoryId())
            .orElseThrow(() -> {
                log.warn("Category with ID {} not found. The category could not be updated", category.getCategoryId());
                return new ResourceNotFoundException("Category", category.getCategoryId());
            });

        log.info("Category with ID {} has been updated", category.getCategoryId());
        return this.categoriesRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Integer id) {
        if(!this.categoriesRepository.existsById(id)) {
            log.warn("Category with ID {} not found. The category could not be deleted", id);
            throw new ResourceNotFoundException("Category", id);
        }

        log.info("Category with ID {} has been deleted", id);
        this.categoriesRepository.deleteById(id);
    }
}
