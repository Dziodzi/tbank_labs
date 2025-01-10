package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "EventController", description = "Event management operations")
public interface EventAPI {
    
    @GetMapping("/api/v2/events")
    ResponseEntity<List<Event>> getAllEvents();
    
    @GetMapping("/api/v2/events/{id}")
    ResponseEntity<Event> getEventById(@PathVariable Long id);
    
    @GetMapping("/api/v2/events/search")
    @Operation(summary = "Search events", description = "Returns a list of events based on search parameters")
    ResponseEntity<List<Event>> searchEvents(
            @RequestParam(required = false) Long placeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    );
    
    @PostMapping("/api/v2/events")
    ResponseEntity<Event> createEvent(@RequestBody Event event);
    
    @PutMapping("/api/v2/events/{id}")
    ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event);
    
    @DeleteMapping("/api/v2/events/{id}")
    ResponseEntity<Void> deleteEvent(@PathVariable Long id);
}
