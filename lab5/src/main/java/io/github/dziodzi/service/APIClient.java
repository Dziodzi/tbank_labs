package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class APIClient {

    private final RestTemplate restTemplate;

    private static final String CATEGORIES_URL = "https://kudago.com/public-api/v1.4/place-categories";
    private static final String LOCATIONS_URL = "https://kudago.com/public-api/v1.4/locations";

    public APIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Category> fetchCategories() {
        Category[] categories = restTemplate.getForObject(CATEGORIES_URL, Category[].class);
        return List.of(categories);
    }

    public List<Location> fetchLocations() {
        Location[] locations = restTemplate.getForObject(LOCATIONS_URL, Location[].class);
        return List.of(locations);
    }
}
