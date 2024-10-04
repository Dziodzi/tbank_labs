package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/api/v1/places/categories")
public interface CategoryApi {

    @Operation(summary = "Get all categories", description = "Returns a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
            @ApiResponse(responseCode = "204", description = "No categories found")
    })
    @GetMapping
    ResponseEntity<Collection<Category>> getAllCategories();

    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<Category> getCategoryById(@Parameter(description = "ID of the category to be retrieved") @PathVariable int id);

    @Operation(summary = "Create a new category", description = "Creates a new category and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @PostMapping
    ResponseEntity<Category> createCategory(@RequestBody Category category);

    @Operation(summary = "Update category", description = "Updates an existing category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    ResponseEntity<Category> updateCategory(@Parameter(description = "ID of the category to be updated") @PathVariable int id, @RequestBody CategoryDTO categoryDTO);

    @Operation(summary = "Delete category", description = "Deletes a category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCategory(@Parameter(description = "ID of the category to be deleted") @PathVariable int id);
}
