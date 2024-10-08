package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService mockCategoryService;

    @Test
    void testGetAllCategories_Success() throws Exception {
        Collection<Category> categories = new ArrayList<>();
        categories.add(new Category(1, new CategoryDTO("Slug1", "Category 1")));
        categories.add(new Category(2, new CategoryDTO("Slug2", "Category 2")));

        when(mockCategoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/places/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].slug").value("Slug1"));
    }

    @Test
    void testGetAllCategories_NoContent() throws Exception {
        when(mockCategoryService.getAllCategories()).thenThrow(new NoContentException("No categories found"));

        mockMvc.perform(get("/api/v1/places/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetCategoryById_Success() throws Exception {
        Category category = new Category(1, new CategoryDTO("Slug", "Category"));
        when(mockCategoryService.getCategoryById(1)).thenReturn(category);

        mockMvc.perform(get("/api/v1/places/categories/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        when(mockCategoryService.getCategoryById(1)).thenThrow(new ResourceNotFoundException("Category with id 1 not found"));

        mockMvc.perform(get("/api/v1/places/categories/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category with id 1 not found"));
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        Category category = new Category(1, new CategoryDTO("Slug", "Category"));
        when(mockCategoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"dto\": {\"slug\": \"Slug\", \"name\": \"Category\"}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateCategory_Conflict() throws Exception {
        when(mockCategoryService.createCategory(any(Category.class))).thenThrow(new IllegalArgumentException("Category with id already exists"));

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"dto\": {\"slug\": \"Slug\", \"name\": \"Category\"}}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Category with id already exists"));
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        CategoryDTO updatedDTO = new CategoryDTO("NewSlug", "New Name");
        Category updatedCategory = new Category(1, updatedDTO);
        when(mockCategoryService.updateCategory(1, updatedDTO)).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"NewSlug\", \"name\": \"New Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        CategoryDTO updatedDTO = new CategoryDTO("NewSlug", "New Name");
        when(mockCategoryService.updateCategory(1, updatedDTO)).thenThrow(new ResourceNotFoundException("Category with id 1 not found"));

        mockMvc.perform(put("/api/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"NewSlug\", \"name\": \"New Name\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category with id 1 not found"));
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        doNothing().when(mockCategoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategory_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Category with id 1 not found")).when(mockCategoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category with id 1 not found"));
    }
}
