package com.vexa.ecommerce.Categories;

import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesServiceTest {

    @Mock
    CategoriesRepository categoriesRepository;

    @InjectMocks
    CategoriesService categoriesService;

    Categories category;

    @BeforeEach
    void setup() {
        category = new Categories();
        category.setCategoryId(1);
        category.setName("Electronics");
    }

    @Test
    void getAllCategories_shouldReturnList() {
        when(categoriesRepository.findAll()).thenReturn(List.of(category));

        List<Categories> result = categoriesService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        when(categoriesRepository.findById(1)).thenReturn(Optional.of(category));

        Categories result = categoriesService.getCategoryById(1);

        assertEquals(1, result.getCategoryId());
    }

    @Test
    void getCategoryById_shouldThrowIfNotFound() {
        when(categoriesRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriesService.getCategoryById(1)
        );
    }

    @Test
    void saveNewCategory_shouldSaveSuccessfully() {
        when(categoriesRepository.existsByName("Electronics")).thenReturn(false);
        when(categoriesRepository.save(category)).thenReturn(category);

        Categories result = categoriesService.saveNewCategory(category);

        assertEquals("Electronics", result.getName());
        verify(categoriesRepository).save(category);
    }

    @Test
    void saveNewCategory_shouldThrowIfNameExists() {
        when(categoriesRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> categoriesService.saveNewCategory(category)
        );
    }

    @Test
    void updateCategory_shouldUpdateSuccessfully() {
        when(categoriesRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoriesRepository.save(category)).thenReturn(category);

        Categories result = categoriesService.updateCategory(category);

        assertEquals(1, result.getCategoryId());
        verify(categoriesRepository).save(category);
    }

    @Test
    void updateCategory_shouldThrowIfNotFound() {
        when(categoriesRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriesService.updateCategory(category)
        );
    }

    @Test
    void deleteCategoryById_shouldDeleteSuccessfully() {
        when(categoriesRepository.existsById(1)).thenReturn(true);

        categoriesService.deleteCategoryById(1);

        verify(categoriesRepository).deleteById(1);
    }

    @Test
    void deleteCategoryById_shouldThrowIfNotFound() {
        when(categoriesRepository.existsById(1)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriesService.deleteCategoryById(1)
        );
    }
}
