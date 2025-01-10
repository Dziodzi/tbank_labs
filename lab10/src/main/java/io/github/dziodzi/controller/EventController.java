package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.EventAPI;
import io.github.dziodzi.entity.Event;
import io.github.dziodzi.service.interfaces.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class EventController implements EventAPI {
    
    private final EventService eventService;
    
    @Override
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    
    @Override
    public ResponseEntity<Event> getEventById(Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }
    
    @Override
    public ResponseEntity<List<Event>> searchEvents(Long placeId, String name, LocalDate fromDate, LocalDate toDate) {
        return ResponseEntity.ok(eventService.searchEvents(placeId, name, fromDate, toDate));
    }
    
    @Override
    public ResponseEntity<Event> createEvent(Event event) {
        log.info(event.toString());
        log.info(event.getPlace().toString());
        return ResponseEntity.status(201).body(eventService.createEvent(event));
    }
    
    @Override
    public ResponseEntity<Event> updateEvent(Long id, Event event) {
        return ResponseEntity.ok(eventService.updateEventById(id, event));
    }
    
    @Override
    public ResponseEntity<Void> deleteEvent(Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }
}
