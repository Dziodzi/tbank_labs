package io.github.dziodzi.service;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.repository.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
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
        LocationDTO updatedDTO = new LocationDTO("Name");
        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of("slug", new LocationDTO("Namy"))));
        when(mockLocationStore.get("slug")).thenReturn(updatedDTO);

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
        LocationDTO dto = new LocationDTO("Updated Location");

        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of("slug", dto)));
        when(mockLocationStore.get("slug")).thenReturn(dto);

        locationService.deleteLocation("slug");

        verify(mockLocationStore).delete("slug");

        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationBySlug("slug"));
        assertEquals("Location with slug slug not found", exception.getMessage());
    }

    @Test
    void testDeleteLocation_NotFound() {
        when(mockLocationStore.get("slug")).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation("slug"));
        assertEquals("Location with slug slug not found", exception.getMessage());
    }

    @Test
    void testGetAllLocations_Success() {
        LocationDTO dto1 = new LocationDTO("Location 1");
        LocationDTO dto2 = new LocationDTO("Location 2");

        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>(Map.of(
                "slug1", dto1,
                "slug2", dto2
        )));

        when(mockLocationStore.get("slug1")).thenReturn(dto1);
        when(mockLocationStore.get("slug2")).thenReturn(dto2);

        Collection<Location> locations = locationService.getAllLocations();

        assertEquals(2, locations.size());
        verify(mockLocationStore, times(3)).getAll();
    }

    @Test
    void testGetAllLocations_NoContent() {
        when(mockLocationStore.getAll()).thenReturn(new ConcurrentHashMap<>());

        Exception exception = assertThrows(NoContentException.class, () -> locationService.getAllLocations());
        assertEquals("No location found", exception.getMessage());
    }

    @Test
    void testInitializeLocations_Success() {
        Collection<Location> locations = new ArrayList<>();
        locations.add(new Location("slug1", new LocationDTO("Location 1")));
        locations.add(new Location("slug2", new LocationDTO("Location 2")));

        locationService.initializeLocations(locations);

        verify(mockLocationStore, times(1)).create("slug1", new LocationDTO("Location 1"));
        verify(mockLocationStore, times(1)).create("slug2", new LocationDTO("Location 2"));
    }
}
