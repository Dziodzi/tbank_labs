package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class APIClient {

    private final RestTemplate restTemplate;

    @Value("${custom.api.categories-url}")
    private String categoriesUrl;

    @Value("${custom.api.locations-url}")
    private String locationsUrl;

    public APIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Category> fetchCategories() {
        try {
            Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
            if (categories == null) {
                return new ArrayList<>();
            }
            return List.of(categories);
        } catch (RestClientException e) {
            log.error("Failed to fetch categories", e);
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }

    public List<Location> fetchLocations() {
        try {
            Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
            if (locations == null) {
                return new ArrayList<>();
            }
            return List.of(locations);
        } catch (RestClientException e) {
            log.error("Failed to fetch locations", e);
            throw new RuntimeException("Failed to fetch locations", e);
        }
    }
}
