package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.EventAPI;
import io.github.dziodzi.entity.Event;
import io.github.dziodzi.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController implements EventAPI {
    
    private final EventService eventService;
    
    @Override
    public CompletableFuture<ResponseEntity<List<Event>>> getEvents(
            double budget, String currency, Long dateFrom, Long dateTo) {
        
        return eventService.getFilteredEvents(budget, currency, dateFrom, dateTo)
                .thenApply(ResponseEntity::ok);
    }
}
