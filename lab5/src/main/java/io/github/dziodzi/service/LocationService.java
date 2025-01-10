package io.github.dziodzi.service;

import io.github.dziodzi.entity.Location;
import io.github.dziodzi.entity.dto.LocationDTO;
import io.github.dziodzi.exception.NoContentException;
import io.github.dziodzi.exception.ResourceNotFoundException;
import io.github.dziodzi.repository.InMemoryStore;
import io.github.dziodzi.service.observer.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService extends Publisher {
    
    private final InMemoryStore<String, LocationDTO> locationStore;
    
    public Collection<Location> getAllLocations() {
        var all = locationStore.getAll();
        if (all.isEmpty()) {
            throw new NoContentException("No locations found");
        }
        Collection<Location> locations = new ArrayList<>();
        all.forEach((key, value) -> locations.add(new Location(key, value)));
        return locations;
    }
    
    public Location getLocationBySlug(String slug) {
        var locationDTO = locationStore.get(slug);
        if (locationDTO == null) {
            throw new ResourceNotFoundException("Location with slug " + slug + " not found");
        }
        return new Location(slug, locationDTO);
    }
    
    public Location createLocation(Location location) {
        if (locationStore.get(location.getSlug()) != null) {
            throw new IllegalArgumentException("Location with slug " + location.getSlug() + " already exists");
        }
        locationStore.create(location.getSlug(), location.toDTO());
        notifySubscribers("Location created: " + location.getSlug());
        return location;
    }
    
    public Location updateLocation(String slug, LocationDTO locationDTO) {
        if (!locationStore.getAll().containsKey(slug)) {
            throw new ResourceNotFoundException("Location with slug " + slug + " not found");
        }
        var currentLocation = locationStore.get(slug);
        locationStore.update(slug, locationDTO);
        notifySubscribers("Location updated: " + slug);
        return new Location(slug, locationDTO);
    }
    
    public void deleteLocation(String slug) {
        if (!locationStore.getAll().containsKey(slug)) {
            throw new ResourceNotFoundException("Location with slug " + slug + " not found");
        }
        locationStore.delete(slug);
        notifySubscribers("Location deleted: " + slug);
    }
    
    
    public void initializeLocations(Collection<Location> locations) {
        for (Location location : locations) {
            locationStore.create(location.getSlug(), location.toDTO());
        }
    }
    
    public Collection<LocationDTO> getLocationSnapshots(String slug) {
        var snapshots = locationStore.getSnapshots(slug);
        if (snapshots.isEmpty()) {
            throw new ResourceNotFoundException("No snapshots found for slug " + slug);
        }
        Collection<LocationDTO> snapshotStates = new ArrayList<>();
        snapshots.forEach(snapshot -> snapshotStates.add(snapshot.getState()));
        return snapshotStates;
    }
}
