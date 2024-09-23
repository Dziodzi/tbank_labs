package io.github.dziodzi.service;

import io.github.dziodzi.entity.Location;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LocationService {

    private final InMemoryStore<Location> locationStore;

    public LocationService(InMemoryStore<Location> locationStore) {
        this.locationStore = locationStore;
    }

    public Collection<Location> getAllLocations() {
        return locationStore.getAll();
    }

    public Location getLocationById(int id) {
        return locationStore.getById(id);
    }

    public Location createLocation(Location location) {
        return locationStore.create(location);
    }

    public Location updateLocation(int id, Location location) {
        return locationStore.update(id, location);
    }

    public void deleteLocation(int id) {
        locationStore.delete(id);
    }
}

