package io.github.dziodzi.service.interfaces;

import io.github.dziodzi.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    List<Event> searchEvents(Long placeId, String name, LocalDate fromDate, LocalDate toDate);
    List<Event> getAllEvents();
    Event createEvent(Event event);
    Event getEventById(Long id);
    Event getEventByName(String name);
    Event updateEventById(Long id, Event event);
    void deleteEventById(Long id);
}
