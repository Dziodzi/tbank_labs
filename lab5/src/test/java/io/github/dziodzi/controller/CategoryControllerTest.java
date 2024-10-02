package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private CategoryService mockCategoryService;
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        mockCategoryService = Mockito.mock(CategoryService.class);
        categoryController = new CategoryController(mockCategoryService);
    }

    @Test
    void testGetAllCategories_Success() {
        Collection<Category> categories = new ArrayList<>();
        categories.add(new Category(1, new CategoryDTO("Slug1", "Category 1")));
        categories.add(new Category(2, new CategoryDTO("Slug2", "Category 2")));

        when(mockCategoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<Collection<Category>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    void testGetAllCategories_NoContent() {
        when(mockCategoryService.getAllCategories()).thenReturn(new ArrayList<>());

        ResponseEntity<Collection<Category>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testGetCategoryById_Success() {
        Category category = new Category(1, new CategoryDTO("Slug", "Category"));
        when(mockCategoryService.getCategoryById(1)).thenReturn(category);

        ResponseEntity<Category> response = categoryController.getCategoryById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(mockCategoryService.getCategoryById(1)).thenReturn(null);

        ResponseEntity<Category> response = categoryController.getCategoryById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateCategory_Success() {
        Category category = new Category(1, new CategoryDTO("Slug", "Category"));
        when(mockCategoryService.createCategory(category)).thenReturn(category);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testCreateCategory_Conflict() {
        Category category = new Category(1, new CategoryDTO("Slug", "Category"));
        when(mockCategoryService.createCategory(category)).thenReturn(null);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateCategory_Success() {
        CategoryDTO updatedDTO = new CategoryDTO("NewSlug", "New Name");
        Category updatedCategory = new Category(1, updatedDTO);
        when(mockCategoryService.updateCategory(1, updatedDTO)).thenReturn(updatedCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(1, updatedDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryDTO updatedDTO = new CategoryDTO("NewSlug", "New Name");
        when(mockCategoryService.updateCategory(1, updatedDTO)).thenReturn(null);

        ResponseEntity<Category> response = categoryController.updateCategory(1, updatedDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteCategory_Success() {
        when(mockCategoryService.deleteCategory(1)).thenReturn(true);

        ResponseEntity<Void> response = categoryController.deleteCategory(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(mockCategoryService.deleteCategory(1)).thenReturn(false);

        ResponseEntity<Void> response = categoryController.deleteCategory(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
