package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.Place;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "PlaceController", description = "Place management operations")
public interface PlaceAPI {
    
    @GetMapping("/api/v2/places")
    @Operation(summary = "Get all places", description = "Returns a list of all places")
    ResponseEntity<List<Place>> getAllPlaces();
    
    @GetMapping("/api/v2/places/{id}")
    @Operation(summary = "Get place by ID", description = "Returns a place by its ID")
    ResponseEntity<Place> getPlaceById(@Valid @PathVariable("id") Long id);
    
    @PostMapping("/api/v2/places")
    @Operation(summary = "Create a new place", description = "Saves a new place to the database")
    ResponseEntity<Place> createPlace(@Valid @RequestBody Place place);
    
    @PutMapping("/api/v2/places/{id}")
    @Operation(summary = "Update place by ID", description = "Updates a place by its ID and returns the updated entity")
    ResponseEntity<Place> updatePlaceById(@Valid @PathVariable("id") Long id, @RequestBody Place place);
    
    @DeleteMapping("/api/v2/places/{id}")
    @Operation(summary = "Delete place by ID", description = "Deletes a place by its ID")
    ResponseEntity<Void> deletePlaceById(@Valid @PathVariable("id") Long id);
}
