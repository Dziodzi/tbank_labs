package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/places/categories")
public class CategoryController {

    private final InMemoryStore<Category> categoryStore;

    public CategoryController(InMemoryStore<Category> categoryStore) {
        this.categoryStore = categoryStore;
    }

    @GetMapping
    public Collection<Category> getAllCategories() {
        return categoryStore.getAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable int id) {
        return categoryStore.getById(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryStore.create(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        return categoryStore.update(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryStore.delete(id);
    }
}