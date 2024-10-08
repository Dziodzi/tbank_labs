package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@LogExecutionTime
@RequiredArgsConstructor
public class DataInitializationService {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final APIClient apiClient;

    public void initializeData() {
        try {
            log.info("-> Categories initialization started.");
            List<Category> categories = apiClient.fetchCategories();
            categoryService.initializeCategories(categories);
            log.info("--> Categories initialization finished.");
        } catch (Exception e) {
            log.error("Failed to initialize categories", e);
            throw new RuntimeException("Failed to initialize categories", e);
        }

        try {
            log.info("-> Locations initialization started.");
            List<Location> locations = apiClient.fetchLocations();
            locationService.initializeLocations(locations);
            log.info("--> Locations initialization finished.");
        } catch (Exception e) {
            log.error("Failed to initialize locations", e);
            throw new RuntimeException("Failed to initialize locations", e);
        }
    }
}
