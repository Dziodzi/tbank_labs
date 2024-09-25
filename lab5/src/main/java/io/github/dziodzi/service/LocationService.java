package io.github.dziodzi.service;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.tools.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@LogExecutionTime
public class LocationService {

    private final InMemoryStore<String, LocationDTO> locationStore;

    public LocationService(InMemoryStore<String, LocationDTO> locationStore) {
        this.locationStore = locationStore;
    }

    public Collection<Location> getAllLocations() {
        var all = locationStore.getAll();
        Collection<Location> locations = new ArrayList<>();
        for (String key : all.keySet()) {
            locations.add(getLocationBySlug(key));
        }
        return locations;
    }

    public Location getLocationBySlug(String key) {
        return new Location(key, locationStore.get(key));
    }

    public Location createLocation(Location location) {
        locationStore.create(location.getSlug(), location.toDTO());
        return location;
    }

    public Location updateLocation(String key, Location location) {
        locationStore.update(key, location.toDTO());
        return getLocationBySlug(key);
    }

    public void deleteLocation(String key) {
        locationStore.delete(key);
    }

    protected void initializeLocations(Collection<Location> locations) {
        for (Location location : locations) {
            locationStore.create(location.getSlug(), location.toDTO());
        }
    }
}
