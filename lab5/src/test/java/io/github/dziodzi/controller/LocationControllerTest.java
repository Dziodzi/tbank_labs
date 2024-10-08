package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService mockLocationService;

    @Test
    void testGetAllLocations_Success() throws Exception {
        Collection<Location> locations = new ArrayList<>();
        locations.add(new Location("slug1", new LocationDTO("Location 1")));
        locations.add(new Location("slug2", new LocationDTO("Location 2")));

        when(mockLocationService.getAllLocations()).thenReturn(locations);

        mockMvc.perform(get("/api/v1/locations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("slug1"))
                .andExpect(jsonPath("$[0].name").value("Location 1"));
    }

    @Test
    void testGetAllLocations_NoContent() throws Exception {
        when(mockLocationService.getAllLocations()).thenThrow(new NoContentException("No locations found"));

        mockMvc.perform(get("/api/v1/locations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLocationBySlug_Success() throws Exception {
        Location location = new Location("slug", new LocationDTO("Location"));
        when(mockLocationService.getLocationBySlug("slug")).thenReturn(location);

        mockMvc.perform(get("/api/v1/locations/slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.name").value("Location"));
    }

    @Test
    void testGetLocationBySlug_NotFound() throws Exception {
        when(mockLocationService.getLocationBySlug("slug")).thenThrow(new ResourceNotFoundException("Location with slug slug not found"));

        mockMvc.perform(get("/api/v1/locations/slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Location with slug slug not found"));
    }

    @Test
    void testCreateLocation_Success() throws Exception {
        Location location = new Location("slug", new LocationDTO("Location"));
        when(mockLocationService.createLocation(any(Location.class))).thenReturn(location);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"slug\", \"dto\": {\"name\": \"Location\"}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("slug"));
    }

    @Test
    void testCreateLocation_Conflict() throws Exception {
        when(mockLocationService.createLocation(any(Location.class))).thenThrow(new IllegalArgumentException("Location with slug already exists"));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"slug\", \"dto\": {\"name\": \"Location\"}}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Location with slug already exists"));
    }

    @Test
    void testUpdateLocation_Success() throws Exception {
        LocationDTO updatedDTO = new LocationDTO("New Location");
        Location updatedLocation = new Location("slug", updatedDTO);
        when(mockLocationService.updateLocation(eq("slug"), any(LocationDTO.class))).thenReturn(updatedLocation);

        mockMvc.perform(put("/api/v1/locations/slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Location\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.name").value("New Location"));
    }

    @Test
    void testUpdateLocation_NotFound() throws Exception {
        LocationDTO updatedDTO = new LocationDTO("New Location");
        when(mockLocationService.updateLocation(eq("slug"), any(LocationDTO.class))).thenThrow(new ResourceNotFoundException("Location with slug slug not found"));

        mockMvc.perform(put("/api/v1/locations/slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Location\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Location with slug slug not found"));
    }

    @Test
    void testDeleteLocation_Success() throws Exception {
        doNothing().when(mockLocationService).deleteLocation("slug");

        mockMvc.perform(delete("/api/v1/locations/slug"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteLocation_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Location with slug slug not found")).when(mockLocationService).deleteLocation("slug");

        mockMvc.perform(delete("/api/v1/locations/slug"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Location with slug slug not found"));
    }
}
