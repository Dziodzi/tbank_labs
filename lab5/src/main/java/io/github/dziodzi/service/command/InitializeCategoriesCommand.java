package io.github.dziodzi.command;

import io.github.dziodzi.entity.Category;
import io.github.dziodzi.service.APIClient;
import io.github.dziodzi.service.CategoryService;
import io.github.dziodzi.service.command.Command;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogExecutionTime
@RequiredArgsConstructor
public class InitializeCategoriesCommand implements Command {
    
    private final CategoryService categoryService;
    private final APIClient apiClient;
    
    @Override
    public void execute() {
        log.info("-> Categories initialization started.");
        categoryService.initializeCategories(apiClient.fetchCategories());
        log.info("--> Categories initialization finished.");
    }
}
