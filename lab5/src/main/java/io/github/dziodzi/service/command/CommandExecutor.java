package io.github.dziodzi.service.command;

import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@LogExecutionTime
public class CommandExecutor {
    private final List<Command> commands;
    
    public CommandExecutor(List<Command> commands) {
        this.commands = commands;
    }
    
    public void executeCommands() {
        for (Command command : commands) {
            try {
                command.execute();
            } catch (Exception e) {
                log.error("Failed to execute command", e);
            }
        }
    }
    
    public void clearCommands() {
        commands.clear();
    }
}
