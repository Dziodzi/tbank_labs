package io.github.dziodzi.service;

import io.github.dziodzi.service.command.Command;
import io.github.dziodzi.service.command.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.mockito.Mockito.*;

class DataInitializationServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationService locationService;

    @Mock
    private APIClient apiClient;
    
    @Mock
    private CommandExecutor commandExecutor;
    
    @Mock
    private Command mockCommand;
    
    private DataInitializationService dataInitializationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInitializationService = new DataInitializationService(
                commandExecutor,
                mock(ThreadPoolTaskScheduler.class)
        );
    }

    
    @Test
    void initializeData_ShouldExecuteCommands() {
        dataInitializationService.initializeData();
        verify(commandExecutor).executeCommands();
    }

    @Test
    void initializeData_WhenExecutionFails_ShouldLogError() {
        doThrow(new RuntimeException("Execution failed")).when(commandExecutor).executeCommands();
        dataInitializationService.initializeData();
        verify(commandExecutor).executeCommands();
    }
}