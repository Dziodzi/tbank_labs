package io.github.dziodzi.config;

import io.github.dziodzi.service.command.CommandExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {
    
    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor();
    }
}
