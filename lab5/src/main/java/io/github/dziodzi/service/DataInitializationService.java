package io.github.dziodzi.service;

import io.github.dziodzi.tools.LogExecutionTime;
import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Slf4j
public class DataInitializationService {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final APIClient apiClient;

    @Autowired
    public DataInitializationService(CategoryService categoryService, LocationService locationService, APIClient apiClient) {
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.apiClient = apiClient;
    }


    //TODO: correctly initialize data
    @LogExecutionTime
    public void initializeData() {
        log.info("Data initialization started");

        List<Category> categories = apiClient.fetchCategories();
        categories.forEach(categoryService::createCategory);

        List<Location> locations = apiClient.fetchLocations();
        locations.forEach(locationService::createLocation);

        log.info("Data initialization finished");
    }
}
