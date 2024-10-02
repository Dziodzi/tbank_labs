package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Get all locations", description = "Returns a list of all locations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved locations"),
            @ApiResponse(responseCode = "204", description = "No locations found")
    })
    @GetMapping
    public ResponseEntity<Collection<Location>> getAllLocations() {
        Collection<Location> locations = locationService.getAllLocations();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(locations);
        }
        return ResponseEntity.ok(locations);
    }

    @Operation(summary = "Get location by slug", description = "Returns a single location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the location"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<Location> getLocationBySlug(@Parameter(description = "Slug of the location to be retrieved") @PathVariable String slug) {
        Optional<Location> location = Optional.ofNullable(locationService.getLocationBySlug(slug));
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Create a new location", description = "Creates a new location and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created successfully"),
            @ApiResponse(responseCode = "409", description = "Location already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        if (locationService.createLocation(location) == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }


    @Operation(summary = "Update location", description = "Updates an existing location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PutMapping("/{slug}")
    public ResponseEntity<Location> updateLocation(@Parameter(description = "Slug of the location to be updated") @PathVariable String slug, @RequestBody LocationDTO locationDTO) {
        Optional<Location> location = Optional.ofNullable(locationService.updateLocation(slug, locationDTO));
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete location", description = "Deletes a location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteLocation(@Parameter(description = "Slug of the location to be deleted") @PathVariable String slug) {
        try {
            if (locationService.deleteLocation(slug)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
