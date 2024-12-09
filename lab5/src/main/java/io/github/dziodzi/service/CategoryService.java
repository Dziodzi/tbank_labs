package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.repository.InMemoryStore;
import io.github.dziodzi.service.observer.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService extends Publisher {
    
    private final InMemoryStore<Integer, CategoryDTO> categoryStore;
    
    public Collection<Category> getAllCategories() {
        var all = categoryStore.getAll();
        if (all.isEmpty()) {
            throw new NoContentException("No categories found");
        }
        Collection<Category> categories = new ArrayList<>();
        all.forEach((id, dto) -> categories.add(new Category(id, dto)));
        return categories;
    }
    
    public Category getCategoryById(int id) {
        var categoryDTO = categoryStore.get(id);
        if (categoryDTO == null) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        return new Category(id, categoryDTO);
    }
    
    public Category createCategory(Category category) {
        if (categoryStore.getAll().containsKey(category.getId())) {
            throw new IllegalArgumentException("Category with id " + category.getId() + " already exists");
        }
        categoryStore.create(category.getId(), category.toDTO());
        notifySubscribers("Category created: " + category.getId());
        return category;
    }
    
    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        var currentCategory = categoryStore.get(id);
        categoryStore.update(id, categoryDTO);
        notifySubscribers("Category updated: " + id);
        return new Category(id, categoryDTO);
    }
    
    public void deleteCategory(int id) {
        if (!categoryStore.getAll().containsKey(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        categoryStore.delete(id);
        notifySubscribers("Category deleted: " + id);
    }
    
    public void initializeCategories(Collection<Category> categories) {
        for (Category category : categories) {
            categoryStore.create(category.getId(), category.toDTO());
        }
    }
    
    public Collection<CategoryDTO> getCategorySnapshots(int id) {
        var snapshots = categoryStore.getSnapshots(id);
        if (snapshots.isEmpty()) {
            throw new ResourceNotFoundException("No snapshots found for id " + id);
        }
        Collection<CategoryDTO> snapshotStates = new ArrayList<>();
        snapshots.forEach(snapshot -> snapshotStates.add(snapshot.getState()));
        return snapshotStates;
    }
}
