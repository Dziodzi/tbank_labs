package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.tools.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@LogExecutionTime
public class CategoryService {

    private final InMemoryStore<Category> categoryStore;

    public CategoryService(InMemoryStore<Category> categoryStore) {
        this.categoryStore = categoryStore;
    }

    public Collection<Category> getAllCategories() {
        return categoryStore.getAll();
    }

    public Category getCategoryById(int id) {
        return categoryStore.getById(id);
    }

    public Category createCategory(Category category) {
        return categoryStore.create(category);
    }

    public Category updateCategory(int id, Category category) {
        return categoryStore.update(id, category);
    }

    public void deleteCategory(int id) {
        categoryStore.delete(id);
    }

    protected void initializeCategories(Collection<Category> categories) {
        for (Category category : categories) {
            categoryStore.create(category);
        }
    }
}
