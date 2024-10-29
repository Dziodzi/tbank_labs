package io.github.dziodzi.service;

import io.github.dziodzi.service.command.CommandExecutor;
import io.github.dziodzi.command.InitializeCategoriesCommand;
import io.github.dziodzi.command.InitializeLocationsCommand;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
@Service
@LogExecutionTime
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationListener<ApplicationStartedEvent> {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final APIClient apiClient;

    @Autowired
    private ExecutorService fixedThreadPool;

    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPool;

    @Autowired
    private final CommandExecutor commandExecutor;
    
    @Value("${custom.initialization.schedule}")
    private Duration initializationSchedule;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initializeData();
        scheduleDataInitialization();
    }

    public void initializeData() {
        commandExecutor.addCommand(new InitializeCategoriesCommand(categoryService, apiClient));
        commandExecutor.addCommand(new InitializeLocationsCommand(locationService, apiClient));

        try {
            fixedThreadPool.submit(commandExecutor::executeCommands).get();
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
        } finally {
            commandExecutor.clearCommands();
        }
    }

    private void scheduleDataInitialization() {
        scheduledThreadPool.scheduleWithFixedDelay(this::initializeData, initializationSchedule.toMillis(), initializationSchedule.toMillis(), TimeUnit.MILLISECONDS);
        log.info("Data will be re-initialized after {} s.", initializationSchedule.toSeconds());
    }
}
