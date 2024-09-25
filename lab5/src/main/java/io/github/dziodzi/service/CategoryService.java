package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.tools.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

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
        return new Category(id, categoryStore.get(id));
    }

    public Category createCategory(Category category) {
        categoryStore.create(category.getId(), category.toDTO());
        return category;
    }

    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        categoryStore.update(id, categoryDTO);
        return getCategoryById(id);
    }

    public void deleteCategory(int id) {
        categoryStore.delete(id);
    }

    protected void initializeCategories(Collection<Category> categories) {
        for (Category category : categories) {
            categoryStore.create(category.getId(), category.toDTO());
        }
    }
}
