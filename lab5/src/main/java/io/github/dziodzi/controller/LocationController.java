package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.LocationApi;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class LocationController implements LocationApi {
    
    private final LocationService locationService;
    
    @Override
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
    
    @Override
    public ResponseEntity<Location> getLocationBySlug(String slug) {
        return ResponseEntity.ok(locationService.getLocationBySlug(slug));
    }
    
    @Override
    public ResponseEntity<Location> createLocation(Location location) {
        return ResponseEntity.status(201).body(locationService.createLocation(location));
    }
    
    @Override
    public ResponseEntity<Location> updateLocation(String slug, LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(slug, locationDTO));
    }
    
    @Override
    public ResponseEntity<Void> deleteLocation(String slug) {
        locationService.deleteLocation(slug);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<Collection<LocationDTO>> getLocationSnapshots(String slug) {
        return ResponseEntity.ok(locationService.getLocationSnapshots(slug));
    }
}
