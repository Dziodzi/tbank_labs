package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@Component
@LogExecutionTime
public class APIClient {
    
    private final RestTemplate restTemplate;
    private final String categoriesUrl;
    private final String locationsUrl;
    
    private final ExecutorService executorService;
    private final Semaphore rateLimiterSemaphore;
    
    public APIClient(RestTemplate restTemplate,
                     @Value("${custom.api.categories-url}") String categoriesUrl,
                     @Value("${custom.api.locations-url}") String locationsUrl,
                     @Value("${custom.api.executor.pool-size}") int poolSize,
                     @Value("${custom.api.rate.limit}") int rateLimit
    ) {
        this.restTemplate = restTemplate;
        this.categoriesUrl = categoriesUrl;
        this.locationsUrl = locationsUrl;
        
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.rateLimiterSemaphore = new Semaphore(rateLimit);
        
        log.info("Categories URL: {}", categoriesUrl);
        log.info("Locations URL: {}", locationsUrl);
        log.info("Executor pool size: {}", poolSize);
        log.info("Rate limit: {}", rateLimit);
    }
    
    public CompletableFuture<List<Category>> fetchCategoriesAsync() {
        return CompletableFuture.supplyAsync(this::fetchCategories, executorService);
    }
    
    public CompletableFuture<List<Location>> fetchLocationsAsync() {
        return CompletableFuture.supplyAsync(this::fetchLocations, executorService);
    }
    
    public List<Category> fetchCategories() {
        if (!rateLimiterSemaphore.tryAcquire()) {
            log.warn("Rate limit exceeded for fetchCategories, returning empty list as fallback");
            return fetchCategoriesFallback(null);
        }
        
        try {
            log.info("Fetching categories from URL: {}", categoriesUrl);
            Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
            if (categories == null) {
                return new ArrayList<>();
            }
            return List.of(categories);
        } catch (RestClientException e) {
            log.error("Failed to fetch categories", e);
            throw new RuntimeException("Failed to fetch categories", e);
        } finally {
            rateLimiterSemaphore.release();
        }
    }
    
    public List<Location> fetchLocations() {
        if (!rateLimiterSemaphore.tryAcquire()) {
            log.warn("Rate limit exceeded for fetchLocations, returning empty list as fallback");
            return fetchLocationsFallback(null);
        }
        
        try {
            log.info("Fetching locations from URL: {}", locationsUrl);
            Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
            if (locations == null) {
                return new ArrayList<>();
            }
            return List.of(locations);
        } catch (RestClientException e) {
            log.error("Failed to fetch locations", e);
            throw new RuntimeException("Failed to fetch locations", e);
        } finally {
            rateLimiterSemaphore.release();
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
