package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.service.LocationService;
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

    @GetMapping
    public ResponseEntity<Collection<Location>> getAllLocations() {
        Collection<Location> locations = locationService.getAllLocations();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(locations);
        }
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{key}")
    public ResponseEntity<Location> getLocationBySlug(@PathVariable String key) {
        Optional<Location> location = Optional.ofNullable(locationService.getLocationBySlug(key));
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        try {
            if (locationService.createLocation(location) == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(location);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<Location> updateLocation(@PathVariable String key, @RequestBody LocationDTO locationDTO) {
        Optional<Location> location = Optional.ofNullable(locationService.updateLocation(key, locationDTO));
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String key) {
        try {
            if (locationService.deleteLocation(key)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

