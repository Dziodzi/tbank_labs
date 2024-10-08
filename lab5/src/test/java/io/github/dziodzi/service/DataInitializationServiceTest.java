package io.github.dziodzi.service;

import io.github.dziodzi.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class DataInitializationServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationService locationService;

    @Mock
    private APIClient apiClient;

    private DataInitializationService dataInitializationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInitializationService = new DataInitializationService(categoryService, locationService, apiClient);
    }

    @Test
    void initializeData_WithEmptyCategories_ShouldLogMessage() {
        when(apiClient.fetchCategories()).thenReturn(Collections.emptyList());
        dataInitializationService.initializeData();
        verify(categoryService).initializeCategories(Collections.emptyList());
    }

    @Test
    void initializeData_WithEmptyLocations_ShouldLogMessage() {
        when(apiClient.fetchCategories()).thenReturn(List.of(new Category()));
        when(apiClient.fetchLocations()).thenReturn(Collections.emptyList());
        dataInitializationService.initializeData();
        verify(locationService).initializeLocations(Collections.emptyList());
    }

    @Test
    void initializeData_WhenCategoriesFetchFails_ShouldThrowRuntimeException() {
        when(apiClient.fetchCategories()).thenThrow(new RuntimeException("Fetch failed"));
        Exception exception = assertThrows(RuntimeException.class, () -> dataInitializationService.initializeData());
        Assertions.assertEquals("Failed to initialize categories", exception.getMessage());
    }

    @Test
    void initializeData_WhenLocationsFetchFails_ShouldThrowRuntimeException() {
        when(apiClient.fetchCategories()).thenReturn(List.of(new Category()));
        when(apiClient.fetchLocations()).thenThrow(new RuntimeException("Fetch failed"));
        Exception exception = assertThrows(RuntimeException.class, () -> dataInitializationService.initializeData());
        Assertions.assertEquals("Failed to initialize locations", exception.getMessage());
    }
}
