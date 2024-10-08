package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.repository.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private InMemoryStore<Integer, CategoryDTO> mockCategoryStore;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        mockCategoryStore = Mockito.mock(InMemoryStore.class);
        categoryService = new CategoryService(mockCategoryStore);
    }

    @Test
    void testCreateCategory_Success() {
        Category category = new Category(1, new CategoryDTO("Slug", "Name"));
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Category createdCategory = categoryService.createCategory(category);

        verify(mockCategoryStore).create(1, category.toDTO());
        assertEquals(category, createdCategory);
    }

    @Test
    void testCreateCategory_CategoryAlreadyExists() {
        Category category = new Category(1, new CategoryDTO("Slug", "Name"));
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(1, category.toDTO())));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(category));
        assertEquals("Category with id 1 already exists", exception.getMessage());
    }

    @Test
    void testGetCategoryById_Success() {
        CategoryDTO dto = new CategoryDTO("Slug", "Name");
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(1, dto)));
        when(mockCategoryStore.get(1)).thenReturn(dto);

        Category category = categoryService.getCategoryById(1);

        assertNotNull(category);
        assertEquals(1, category.getId());
        assertEquals("Name", category.getName());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1));
        assertEquals("Category with id 1 not found", exception.getMessage());
    }

    @Test
    void testUpdateCategory_Success() {
        CategoryDTO updatedDTO = new CategoryDTO("Slug", "Name");
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(1, new CategoryDTO("Sluggy", "Namy"))));
        when(mockCategoryStore.get(1)).thenReturn(updatedDTO);

        Category updatedCategory = categoryService.updateCategory(1, updatedDTO);

        verify(mockCategoryStore).update(1, updatedDTO);
        assertNotNull(updatedCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryDTO updatedDTO = new CategoryDTO("Slug", "Name");
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1, updatedDTO));
        assertEquals("Category with id 1 not found", exception.getMessage());
    }

    @Test
    void testDeleteCategory_Success() {
        CategoryDTO dto = new CategoryDTO("Slug", "Name");

        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(1, dto)));
        when(mockCategoryStore.get(1)).thenReturn(dto);

        categoryService.deleteCategory(1);

        verify(mockCategoryStore).delete(1);

        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1));
        assertEquals("Category with id 1 not found", exception.getMessage());
    }


    @Test
    void testDeleteCategory_NotFound() {;
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1));
        assertEquals("Category with id 1 not found", exception.getMessage());
    }

    @Test
    void testGetAllCategories_Success() {
        CategoryDTO dto1 = new CategoryDTO("Slug1", "Name1");
        CategoryDTO dto2 = new CategoryDTO("Slug2", "Name2");

        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(
                1, dto1,
                2, dto2
        )));

        when(mockCategoryStore.get(1)).thenReturn(dto1);
        when(mockCategoryStore.get(2)).thenReturn(dto2);

        Collection<Category> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        verify(mockCategoryStore, times(3)).getAll();
    }

    @Test
    void testGetAllCategories_NoContent() {
        when(mockCategoryStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(NoContentException.class, () -> categoryService.getAllCategories());
        assertEquals("No categories found", exception.getMessage());
    }

    @Test
    void testInitializeCategories_Success() {
        Collection<Category> categories = new ArrayList<>();
        categories.add(new Category(1, new CategoryDTO("Slug1", "Name1")));
        categories.add(new Category(2, new CategoryDTO("Slug2", "Name2")));

        categoryService.initializeCategories(categories);

        verify(mockCategoryStore, times(1)).create(1, new CategoryDTO("Slug1", "Name1"));
        verify(mockCategoryStore, times(1)).create(2, new CategoryDTO("Slug2", "Name2"));
    }
}
