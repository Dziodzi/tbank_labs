package io.github.dziodzi.service;

import io.github.dziodzi.entity.Event;
import io.github.dziodzi.entity.Place;
import io.github.dziodzi.exception.AlreadyExistsException;
import io.github.dziodzi.exception.NotFoundException;
import io.github.dziodzi.exception.InvalidBodyException;
import io.github.dziodzi.repository.EventRepository;
import io.github.dziodzi.repository.PlaceRepository;
import io.github.dziodzi.service.interfaces.EventService;
import io.github.dziodzi.tools.LogExecutionTime;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@LogExecutionTime
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    
    @Override
    public List<Event> searchEvents(Long placeId, String name, LocalDate fromDate, LocalDate toDate) {
        Specification<Event> specification = Specification.where(hasPlace(placeId))
                .and(hasName(name))
                .and(betweenDates(fromDate, toDate))
                .and(fetchPlace());
        
        return eventRepository.findAll(specification);
    }
    
    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    @Override
    public Event createEvent(Event event) {
        validateEvent(event);
        
        if (event.getId() == null && eventRepository.existsByName(event.getName())) {
            throw new AlreadyExistsException("Event with name '" + event.getName() + "' already exists.");
        }
        
        return eventRepository.save(event);
    }
    
    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with ID " + id + " not found."));
    }
    
    @Override
    public Event getEventByName(String name) {
        return eventRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Event with name '" + name + "' not found."));
    }
    
    @Override
    public Event updateEventById(Long id, Event event) {
        Event existedEvent = getEventById(id);
        
        existedEvent.setName(event.getName());
        existedEvent.setPlace(event.getPlace());
        existedEvent.setDate(event.getDate());
        
        return eventRepository.save(existedEvent);
    }
    
    @Override
    public void deleteEventById(Long id) {
        Event existedEvent = getEventById(id);
        eventRepository.delete(existedEvent);
    }
    
    private void validateEvent(Event event) {
        if (event == null) {
            throw new InvalidBodyException("Event is missing.");
        }
        
        if (event.getPlace() == null || event.getPlace().getId() == null || !placeRepository.existsById(event.getPlace().getId())) {
            throw new NotFoundException("Place not found for ID: " + event.getPlace().getId());
        }
    }
    
    private Specification<Event> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }
    
    private Specification<Event> hasPlace(Long placeId) {
        return (root, query, criteriaBuilder) -> {
            if (placeId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Event, Place> placeJoin = root.join("place");
            return criteriaBuilder.equal(placeJoin.get("id"), placeId);
        };
    }
    
    private Specification<Event> betweenDates(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            } else if (fromDate != null && toDate != null) {
                return criteriaBuilder.between(root.get("date"), fromDate, toDate);
            } else if (fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), fromDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("date"), toDate);
            }
        };
    }
    
    private Specification<Event> fetchPlace() {
        return (root, query, criteriaBuilder) -> {
            root.fetch("place", JoinType.LEFT);
            return criteriaBuilder.conjunction();
        };
    }
}
