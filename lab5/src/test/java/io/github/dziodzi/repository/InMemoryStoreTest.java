package io.github.dziodzi.repository;

import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.entity.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStoreTest {

    private InMemoryStore<String, LocationDTO> locationStore;
    private InMemoryStore<Integer, CategoryDTO> categoryStore;

    @BeforeEach
    void setUp() {
        locationStore = new InMemoryStore<>();
        categoryStore = new InMemoryStore<>();
    }

    @Test
    void testCreateAndGetLocation_Success() {
        LocationDTO locationDTO = new LocationDTO("Test Location");
        locationStore.create("slug1", locationDTO);

        LocationDTO fetchedLocation = locationStore.get("slug1");

        assertNotNull(fetchedLocation);
        assertEquals("Test Location", fetchedLocation.name());
    }

    @Test
    void testCreateAndGetCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO("Test Slug", "Test Category");
        categoryStore.create(1, categoryDTO);

        CategoryDTO fetchedCategory = categoryStore.get(1);

        assertNotNull(fetchedCategory);
        assertEquals("Test Category", fetchedCategory.name());
    }

    @Test
    void testUpdateLocation_Success() {
        LocationDTO locationDTO = new LocationDTO("Test Location");
        locationStore.create("slug1", locationDTO);

        LocationDTO updatedLocation = new LocationDTO("Updated Location");
        locationStore.update("slug1", updatedLocation);

        LocationDTO fetchedLocation = locationStore.get("slug1");

        assertNotNull(fetchedLocation);
        assertEquals("Updated Location", fetchedLocation.name());
    }

    @Test
    void testUpdateCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO("Test Slug", "Test Category");
        categoryStore.create(1, categoryDTO);

        CategoryDTO updatedCategory = new CategoryDTO("Updated Slug", "Updated Category");
        categoryStore.update(1, updatedCategory);

        CategoryDTO fetchedCategory = categoryStore.get(1);

        assertNotNull(fetchedCategory);
        assertEquals("Updated Category", fetchedCategory.name());
    }

    @Test
    void testDeleteLocation_Success() {
        LocationDTO locationDTO = new LocationDTO("Test Location");
        locationStore.create("slug1", locationDTO);

        locationStore.delete("slug1");

        assertNull(locationStore.get("slug1"));
    }

    @Test
    void testDeleteCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO("Test Slug", "Test Category");
        categoryStore.create(1, categoryDTO);

        categoryStore.delete(1);

        assertNull(categoryStore.get(1));
    }

    @Test
    void testGetAllLocations_Success() {
        LocationDTO location1 = new LocationDTO("Location 1");
        LocationDTO location2 = new LocationDTO("Location 2");

        locationStore.create("slug1", location1);
        locationStore.create("slug2", location2);

        var allLocations = locationStore.getAll();

        assertEquals(2, allLocations.size());
        assertTrue(allLocations.containsKey("slug1"));
        assertTrue(allLocations.containsKey("slug2"));
    }

    @Test
    void testGetAllCategories_Success() {
        CategoryDTO category1 = new CategoryDTO("Slug1", "Category 1");
        CategoryDTO category2 = new CategoryDTO("Slug2", "Category 2");

        categoryStore.create(1, category1);
        categoryStore.create(2, category2);

        var allCategories = categoryStore.getAll();

        assertEquals(2, allCategories.size());
        assertTrue(allCategories.containsKey(1));
        assertTrue(allCategories.containsKey(2));
    }
}
