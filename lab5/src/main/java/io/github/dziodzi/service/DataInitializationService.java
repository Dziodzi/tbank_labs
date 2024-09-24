package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@LogExecutionTime
public class DataInitializationService {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final APIClient apiClient;

    public DataInitializationService(CategoryService categoryService, LocationService locationService, APIClient apiClient) {
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.apiClient = apiClient;
    }

    public void initializeData() {
        log.info("-> Categories initialization started.");
        List<Category> categories = apiClient.fetchCategories();
        categoryService.initializeCategories(categories);
        log.info("--> Categories initialization finished.");

        log.info("-> Locations initialization started.");
        List<Location> locations = apiClient.fetchLocations();
        locationService.initializeLocations(locations);
        log.info("--> Locations initialization finished.");
    }
}
