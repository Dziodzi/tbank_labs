package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.CategoryApi;
import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<Collection<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Override
    public ResponseEntity<Category> getCategoryById(int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Override
    public ResponseEntity<Category> createCategory(Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @Override
    public ResponseEntity<Category> updateCategory(int id, CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
