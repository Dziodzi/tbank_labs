package io.github.dziodzi.config;

import io.github.dziodzi.service.CategoryService;
import io.github.dziodzi.service.LocationService;
import io.github.dziodzi.service.observer.LoggingSubscriber;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriberConfig {
    
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final LoggingSubscriber loggingSubscriber;
    
    public SubscriberConfig(CategoryService categoryService, LocationService locationService, LoggingSubscriber loggingSubscriber) {
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.loggingSubscriber = loggingSubscriber;
    }
    
    @PostConstruct
    public void registerSubscribers() {
        categoryService.subscribe(loggingSubscriber);
        locationService.subscribe(loggingSubscriber);
    }
    
    @PreDestroy
    public void unregisterSubscribers() {
        categoryService.unsubscribe(loggingSubscriber);
        locationService.unsubscribe(loggingSubscriber);
    }
}
