package io.github.dziodzi.tools;

import io.github.dziodzi.service.DataInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppInitializer {

    private final DataInitializationService dataInitializationService;

    @Value("${custom.links.base-url}")
    private String baseUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            log.info("Starting data initialization...");
            dataInitializationService.initializeData();
            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during data initialization: ", e);
        }

        log.info("Swagger UI is available at: {}/swagger-ui.html", baseUrl);
        log.info("API documentation is available at: {}/v3/api-docs", baseUrl);
    }
}
