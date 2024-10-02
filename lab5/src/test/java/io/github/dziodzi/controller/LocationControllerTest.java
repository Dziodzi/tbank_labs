package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    private LocationService mockLocationService;
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        mockLocationService = Mockito.mock(LocationService.class);
        locationController = new LocationController(mockLocationService);
    }

    @Test
    void testGetAllLocations_Success() {
        Collection<Location> locations = new ArrayList<>();
        locations.add(new Location("slug1", new LocationDTO("Location 1")));
        locations.add(new Location("slug2", new LocationDTO("Location 2")));

        when(mockLocationService.getAllLocations()).thenReturn(locations);

        ResponseEntity<Collection<Location>> response = locationController.getAllLocations();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(locations, response.getBody());
    }

    @Test
    void testGetAllLocations_NoContent() {
        when(mockLocationService.getAllLocations()).thenReturn(new ArrayList<>());

        ResponseEntity<Collection<Location>> response = locationController.getAllLocations();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testGetLocationBySlug_Success() {
        Location location = new Location("slug", new LocationDTO("Location"));
        when(mockLocationService.getLocationBySlug("slug")).thenReturn(location);

        ResponseEntity<Location> response = locationController.getLocationBySlug("slug");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(location, response.getBody());
    }

    @Test
    void testGetLocationBySlug_NotFound() {
        when(mockLocationService.getLocationBySlug("slug")).thenReturn(null);

        ResponseEntity<Location> response = locationController.getLocationBySlug("slug");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateLocation_Success() {
        Location location = new Location("slug", new LocationDTO("Location"));
        when(mockLocationService.createLocation(location)).thenReturn(location);

        ResponseEntity<Location> response = locationController.createLocation(location);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(location, response.getBody());
    }

    @Test
    void testCreateLocation_Conflict() {
        Location location = new Location("slug", new LocationDTO("Location"));
        when(mockLocationService.createLocation(location)).thenReturn(null);

        ResponseEntity<Location> response = locationController.createLocation(location);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateLocation_Success() {
        LocationDTO updatedDTO = new LocationDTO("New Location");
        Location updatedLocation = new Location("slug", updatedDTO);
        when(mockLocationService.updateLocation("slug", updatedDTO)).thenReturn(updatedLocation);

        ResponseEntity<Location> response = locationController.updateLocation("slug", updatedDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedLocation, response.getBody());
    }

    @Test
    void testUpdateLocation_NotFound() {
        LocationDTO updatedDTO = new LocationDTO("New Location");
        when(mockLocationService.updateLocation("slug", updatedDTO)).thenReturn(null);

        ResponseEntity<Location> response = locationController.updateLocation("slug", updatedDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteLocation_Success() {
        when(mockLocationService.deleteLocation("slug")).thenReturn(true);

        ResponseEntity<Void> response = locationController.deleteLocation("slug");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteLocation_NotFound() {
        when(mockLocationService.deleteLocation("slug")).thenReturn(false);

        ResponseEntity<Void> response = locationController.deleteLocation("slug");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
