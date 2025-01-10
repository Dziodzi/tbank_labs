package io.github.dziodzi.service;

import io.github.dziodzi.service.command.CommandExecutor;
import io.github.dziodzi.tools.LogExecutionTime;
import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@LogExecutionTime
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationListener<ApplicationStartedEvent> {
    
    private final CommandExecutor commandExecutor;
    private final ThreadPoolTaskScheduler taskScheduler;
    
    @Value("${custom.initialization.schedule}")
    private Duration initializationSchedule;
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initializeData();
        scheduleDataInitialization();
    }
    
    public void initializeData() {
        try {
            commandExecutor.executeCommands();
            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
        }
    }
    
    private void scheduleDataInitialization() {
        taskScheduler.scheduleWithFixedDelay(this::initializeData, initializationSchedule);
        log.info("Data re-initialization scheduled every {} seconds.", initializationSchedule.toSeconds());
    }
}
