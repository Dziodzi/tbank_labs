package io.github.dziodzi.controller;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final InMemoryStore<Location> locationStore;

    public LocationController(InMemoryStore<Location> locationStore) {
        this.locationStore = locationStore;
    }

    @GetMapping
    public Collection<Location> getAllLocations() {
        return locationStore.getAll();
    }

    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable int id) {
        return locationStore.getById(id);
    }

    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationStore.create(location);
    }

    @PutMapping("/{id}")
    public Location updateLocation(@PathVariable int id, @RequestBody Location location) {
        return locationStore.update(id, location);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable int id) {
        locationStore.delete(id);
    }
}
