package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public Collection<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{key}")
    public Location getLocationBySlug(@PathVariable String key) {
        return locationService.getLocationBySlug(key);
    }

    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.createLocation(location);
    }

    @PutMapping("/{key}")
    public Location updateLocation(@PathVariable String key, @RequestBody Location location) {
        return locationService.updateLocation(key, location);
    }

    @DeleteMapping("/{key}")
    public void deleteLocation(@PathVariable String key) {
        locationService.deleteLocation(key);
    }
}
