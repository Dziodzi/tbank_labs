package io.github.dziodzi.service;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationServiceTest {

    private InMemoryStore<String, LocationDTO> mockLocationStore;
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        mockLocationStore = Mockito.mock(InMemoryStore.class);
        locationService = new LocationService(mockLocationStore);
    }

    @Test
    void testCreateLocation_Success() {
        Location location = new Location("slug", new LocationDTO("Test Location"));
        when(mockLocationStore.get(location.getSlug())).thenReturn(null);

        Location createdLocation = locationService.createLocation(location);

        verify(mockLocationStore).create(location.getSlug(), location.toDTO());
        assertEquals(location, createdLocation);
    }

    @Test
    void testCreateLocation_LocationAlreadyExists() {
        Location location = new Location("slug", new LocationDTO("Test Location"));
        when(mockLocationStore.get(location.getSlug())).thenReturn(new LocationDTO("Test Location"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> locationService.createLocation(location));
        assertEquals("Location with slug slug already exists", exception.getMessage());
    }

    @Test
    void testGetLocationBySlug_Success() {
        LocationDTO dto = new LocationDTO("Test Location");
        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of("slug", dto)));
        when(mockLocationStore.get("slug")).thenReturn(dto);

        Location location = locationService.getLocationBySlug("slug");

        assertNotNull(location);
        assertEquals("slug", location.getSlug());
        assertEquals("Test Location", location.getName());
    }

    @Test
    void testGetLocationBySlug_NotFound() {
        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationBySlug("slug"));
        assertEquals("Location with slug slug not found", exception.getMessage());
    }

    @Test
    void testUpdateLocation_Success() {
        LocationDTO updatedDTO = new LocationDTO("Updated Location");
        when(mockLocationStore.get("slug")).thenReturn(new LocationDTO("Old Location"));

        Location updatedLocation = locationService.updateLocation("slug", updatedDTO);

        verify(mockLocationStore).update("slug", updatedDTO);
        assertNotNull(updatedLocation);
    }

    @Test
    void testUpdateLocation_NotFound() {
        LocationDTO updatedDTO = new LocationDTO("Updated Location");
        when(mockLocationStore.get("slug")).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.updateLocation("slug", updatedDTO));
        assertEquals("Location with slug slug not found", exception.getMessage());
    }

    @Test
    void testDeleteLocation_Success() {
        when(mockLocationStore.get("slug")).thenReturn(new LocationDTO("Test Location"));

        boolean isDeleted = locationService.deleteLocation("slug");

        assertTrue(isDeleted);
        verify(mockLocationStore).delete("slug");
    }

    @Test
    void testDeleteLocation_NotFound() {
        when(mockLocationStore.get("slug")).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation("slug"));
        assertEquals("Location with slug slug not found", exception.getMessage());
    }
}
