package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class APIClientTest {
    
    @Autowired
    private APIClient apiClient;
    
    @Test
    public void shouldFetchCategoriesWithinRateLimit() throws ExecutionException, InterruptedException {
        int allowedRequests = 5;
        List<CompletableFuture<List<Category>>> futures = new ArrayList<>();
        
        for (int i = 0; i < allowedRequests; i++) {
            futures.add(apiClient.fetchCategoriesAsync());
        }
        
        for (CompletableFuture<List<Category>> future : futures) {
            List<Category> categories = future.get();
            assertThat(categories).isNotNull();
        }
    }
    
    @Test
    public void shouldFetchLocationsWithinRateLimit() throws ExecutionException, InterruptedException {
        int allowedRequests = 5;
        List<CompletableFuture<List<Location>>> futures = new ArrayList<>();
        
        for (int i = 0; i < allowedRequests; i++) {
            futures.add(apiClient.fetchLocationsAsync());
        }
        
        for (CompletableFuture<List<Location>> future : futures) {
            List<Location> locations = future.get();
            assertThat(locations).isNotNull();
        }
    }
    
    @Test
    public void shouldReturnFallbackWhenRateLimitExceeded() throws ExecutionException, InterruptedException {
        int requestsToExceedLimit = 10;
        
        List<CompletableFuture<List<Category>>> futures = new ArrayList<>();
        for (int i = 0; i < requestsToExceedLimit; i++) {
            futures.add(apiClient.fetchCategoriesAsync());
            Thread.sleep(50);
        }
        
        int fallbackCount = 0;
        for (CompletableFuture<List<Category>> future : futures) {
            List<Category> categories = future.get();
            if (categories.isEmpty()) {
                fallbackCount++;
            }
        }
        
        assertThat(fallbackCount).isGreaterThan(0);
    }
    
    @Test
    public void shouldReturnFallbackForLocationsWhenRateLimitExceeded() throws ExecutionException, InterruptedException {
        int requestsToExceedLimit = 10;
        
        List<CompletableFuture<List<Location>>> futures = new ArrayList<>();
        for (int i = 0; i < requestsToExceedLimit; i++) {
            futures.add(apiClient.fetchLocationsAsync());
            Thread.sleep(50);
        }
        
        int fallbackCount = 0;
        for (CompletableFuture<List<Location>> future : futures) {
            List<Location> locations = future.get();
            if (locations.isEmpty()) {
                fallbackCount++;
            }
        }
        
        assertThat(fallbackCount).isGreaterThan(0);
    }
    
}
