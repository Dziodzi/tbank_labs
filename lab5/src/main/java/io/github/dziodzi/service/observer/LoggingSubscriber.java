package io.github.dziodzi.service.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingSubscriber implements Subscriber {
    
    @Override
    public void update(String message) {
        log.info("Update received: {}", message);
    }
}
