package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/api/v1/locations")
public interface LocationApi {

    @Operation(summary = "Get all locations", description = "Returns a list of all locations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved locations"),
            @ApiResponse(responseCode = "204", description = "No locations found")
    })
    @GetMapping
    ResponseEntity<Collection<Location>> getAllLocations();

    @Operation(summary = "Get location by slug", description = "Returns a single location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the location"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{slug}")
    ResponseEntity<Location> getLocationBySlug(@Parameter(description = "Slug of the location to be retrieved") @PathVariable String slug);

    @Operation(summary = "Create a new location", description = "Creates a new location and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created successfully"),
            @ApiResponse(responseCode = "409", description = "Location already exists")
    })
    @PostMapping
    ResponseEntity<Location> createLocation(@RequestBody Location location);

    @Operation(summary = "Update location", description = "Updates an existing location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PutMapping("/{slug}")
    ResponseEntity<Location> updateLocation(@Parameter(description = "Slug of the location to be updated") @PathVariable String slug, @RequestBody LocationDTO locationDTO);

    @Operation(summary = "Delete location", description = "Deletes a location by its slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @DeleteMapping("/{slug}")
    ResponseEntity<Void> deleteLocation(@Parameter(description = "Slug of the location to be deleted") @PathVariable String slug);
}
