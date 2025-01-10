package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.PlaceAPI;
import io.github.dziodzi.entity.Place;
import io.github.dziodzi.service.interfaces.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController implements PlaceAPI {
    
    private final PlaceService placeService;
    
    @Override
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }
    
    @Override
    public ResponseEntity<Place> getPlaceById(@Valid Long id) {
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }
    
    @Override
    public ResponseEntity<Place> createPlace(@Valid Place place) {
        return ResponseEntity.status(201).body(placeService.createPlace(place));
    }
    
    @Override
    public ResponseEntity<Place> updatePlaceById(@Valid Long id, Place place) {
        return ResponseEntity.ok(placeService.updatePlaceById(id, place));
    }
    
    @Override
    public ResponseEntity<Void> deletePlaceById(@Valid Long id) {
        placeService.deletePlaceById(id);
        return ResponseEntity.noContent().build();
    }
}
