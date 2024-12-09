package io.github.dziodzi.service.command;

import io.github.dziodzi.service.APIClient;
import io.github.dziodzi.service.LocationService;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@LogExecutionTime
@RequiredArgsConstructor
public class InitializeLocationsCommand implements Command {
    private final LocationService locationService;
    private final APIClient apiClient;
    
    @Override
    public void execute() {
        log.info("-> Locations initialization started.");
        locationService.initializeLocations(apiClient.fetchLocations());
        log.info("--> Locations initialization finished.");
    }
}