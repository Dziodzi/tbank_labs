package io.github.dziodzi.service.interfaces;

import io.github.dziodzi.entity.Place;

import java.util.List;

public interface PlaceService {
    List<Place> getAllPlaces();
    Place createPlace(Place place);
    Place getPlaceById(Long id);
    Place getPlaceByName(String name);
    Place updatePlaceById(Long id, Place place);
    void deletePlaceById(Long id);
}
