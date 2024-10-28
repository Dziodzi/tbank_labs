package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.tools.LogExecutionTime;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@LogExecutionTime
public class APIClient {
    
    private final RestTemplate restTemplate;
    private final String categoriesUrl;
    private final String locationsUrl;
    
    public APIClient(RestTemplate restTemplate,
                     @Value("${custom.api.categories-url}") String categoriesUrl,
                     @Value("${custom.api.locations-url}") String locationsUrl) {
        
        this.restTemplate = restTemplate;
        this.categoriesUrl = categoriesUrl;
        this.locationsUrl = locationsUrl;
        
        log.info("Categories URL: {}", categoriesUrl);
        log.info("Locations URL: {}", locationsUrl);
    }
    
    @RateLimiter(name = "apiClientRateLimiter", fallbackMethod = "fetchCategoriesFallback")
    public List<Category> fetchCategories() {
        log.info("Fetching categories from URL: {}", categoriesUrl);
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
    
    @RateLimiter(name = "apiClientRateLimiter", fallbackMethod = "fetchLocationsFallback")
    public List<Location> fetchLocations() {
        log.info("Fetching locations from URL: {}", locationsUrl);
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
    
    public List<Category> fetchCategoriesFallback(Throwable t) {
        log.warn("Rate limit exceeded for fetchCategories, returning empty list as fallback", t);
        return new ArrayList<>();
    }
    
    public List<Location> fetchLocationsFallback(Throwable t) {
        log.warn("Rate limit exceeded for fetchLocations, returning empty list as fallback", t);
        return new ArrayList<>();
    }
}
