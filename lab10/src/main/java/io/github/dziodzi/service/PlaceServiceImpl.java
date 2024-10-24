package io.github.dziodzi.service;

import io.github.dziodzi.entity.Place;
import io.github.dziodzi.exception.AlreadyExistsException;
import io.github.dziodzi.exception.InvalidBodyException;
import io.github.dziodzi.exception.NotFoundException;
import io.github.dziodzi.repository.PlaceRepository;
import io.github.dziodzi.service.interfaces.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    
    private final PlaceRepository placeRepository;
    
    @Override
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
    
    @Override
    public Place createPlace(Place place) {
        validatePlace(place);
        
        if (place.getId() == null) {
            if (placeRepository.existsByName(place.getName())) {
                throw new AlreadyExistsException("Place with name '" + place.getName() + "' already exists.");
            }
            return placeRepository.save(place);
        } else {
            if (placeRepository.existsById(place.getId())) {
                throw new AlreadyExistsException("Place with ID " + place.getId() + " already exists.");
            }
            return placeRepository.save(place);
        }
    }
    
    @Override
    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Place with ID " + id + " not found."));
    }
    
    @Override
    public Place getPlaceByName(String name) {
        return placeRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Place with name '" + name + "' not found."));
    }
    
    @Override
    public Place updatePlaceById(Long id, Place place) {
        Place existedPlace = getPlaceById(id);
        
        existedPlace.setSlug(place.getSlug());
        existedPlace.setName(place.getName());
        existedPlace.setEvents(place.getEvents());
        
        return placeRepository.save(existedPlace);
    }
    
    @Override
    public void deletePlaceById(Long id) {
        Place existedPlace = getPlaceById(id);
        placeRepository.delete(existedPlace);
    }
    
    private void validatePlace(Place place) {
        if (place == null) {
            throw new InvalidBodyException("Place is missing.");
        }
        
        if (place.getName() == null || place.getName().isEmpty()) {
            throw new InvalidBodyException("Place name is required.");
        }
    }
}
