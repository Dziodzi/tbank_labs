package io.github.dziodzi.entity;

import io.github.dziodzi.entity.dto.CategoryDTO;
import lombok.Getter;

@Getter
public class Category {
    private int id;
    private String slug;
    private String name;

    public Category() {
    }

    public Category(int id, CategoryDTO dto) {
        this.id = id;
        this.slug = dto.slug();
        this.name = dto.name();
    }

    public CategoryDTO toDTO() {
        return new CategoryDTO(this.slug, this.name);
    }
}