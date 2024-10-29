package io.github.dziodzi.service.command;

import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@LogExecutionTime
public class CommandExecutor {
    
    private final List<Command> commands = new ArrayList<>();
    
    public void addCommand(Command command) {
        commands.add(command);
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
