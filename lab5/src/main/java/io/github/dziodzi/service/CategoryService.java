package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@LogExecutionTime
public class CategoryService {

    private final InMemoryStore<Integer, CategoryDTO> categoryStore;

    public CategoryService(InMemoryStore<Integer, CategoryDTO> categoryStore) {
        this.categoryStore = categoryStore;
    }

    public Collection<Category> getAllCategories() {
        var all = categoryStore.getAll();
        Collection<Category> categories = new ArrayList<>();
        for (Integer key : all.keySet()) {
            categories.add(getCategoryById(key));
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            log.warn("Category with id {} not found for GET operation", id);
            return null;
        }
        return new Category(id, categoryStore.get(id));
    }

    public Category createCategory(Category category) {
        if (categoryStore.getAll().containsKey(category.getId())) {
            log.warn("Category with id {} already exists", category.getId());
            return null;
        }
        categoryStore.create(category.getId(), category.toDTO());
        return category;
    }

    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        if (!categoryStore.getAll().containsKey(id)) {
            log.warn("Category with id {} not found for UPDATE operation", id);
            return null;
        }
        categoryStore.update(id, categoryDTO);
        return getCategoryById(id);
    }

    public boolean deleteCategory(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            log.warn("Category with id {} not found for DELETE operation", id);
            return false;
        }
        categoryStore.delete(id);
        return true;
    }

    protected void initializeCategories(Collection<Category> categories) {
        for (Category category : categories) {
            categoryStore.create(category.getId(), category.toDTO());
        }
    }
}
