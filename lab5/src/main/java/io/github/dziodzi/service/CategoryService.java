package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.repository.InMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final InMemoryStore<Integer, CategoryDTO> categoryStore;

    public Collection<Category> getAllCategories() {
        var all = categoryStore.getAll();
        Collection<Category> categories = new ArrayList<>();
        for (Integer key : all.keySet()) {
            categories.add(getCategoryById(key));
        }
        if (categories.isEmpty()) {
            throw new NoContentException("No categories found");
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        return new Category(id, categoryStore.get(id));
    }

    public Category createCategory(Category category) {
        if (categoryStore.getAll().containsKey(category.getId())) {
            throw new IllegalArgumentException("Category with id " + category.getId() + " already exists");
        }
        categoryStore.create(category.getId(), category.toDTO());
        return category;
    }

    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        CategoryDTO currentCategoryDTO = categoryStore.get(id);
        categoryStore.update(id, categoryDTO);

        categoryStore.createSnapshot(id, currentCategoryDTO);
        return getCategoryById(id);
    }


    public void deleteCategory(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        categoryStore.delete(id);
    }

    public void initializeCategories(Collection<Category> categories) {
        for (Category category : categories) {
            categoryStore.create(category.getId(), category.toDTO());
        }
    }
    
    public Collection<CategoryDTO> getCategorySnapshots(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        return new ArrayList<>(categoryStore.getSnapshots(id).values());
    }
}
