package io.github.dziodzi.entity;

import io.github.dziodzi.entity.dto.LocationDTO;
import lombok.Getter;

@Getter
public class Location {
    private String slug;
    private String name;

    public Location() {
    }

    public Location(String key, LocationDTO dto) {
        this.slug = key;
        this.name = dto.name();
    }

    public LocationDTO toDTO() {
        return new LocationDTO(this.name);
    }
}