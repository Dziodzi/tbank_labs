package io.github.dziodzi.tools;

import io.github.dziodzi.service.DataInitializationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppInitializer {

    private final DataInitializationService dataInitializationService;

    public AppInitializer(DataInitializationService dataInitializationService) {
        this.dataInitializationService = dataInitializationService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            log.info("Starting data initialization...");
            dataInitializationService.initializeData();
            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during data initialization: ", e);
        }

        log.info("Swagger UI is available at: http://localhost:8080/swagger-ui.html");
        log.info("API documentation is available at: http://localhost:8080/v3/api-docs");
    }
}
