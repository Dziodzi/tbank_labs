package io.github.dziodzi;

import io.github.dziodzi.service.DataInitializationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication(scanBasePackages = "io.github.dziodzi")
public class AppConfig {

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public CommandLineRunner init(DataInitializationService dataInitializationService) {
        return args -> {
            try {
                log.info("Starting data initialization...");
                dataInitializationService.initializeData();
                log.info("Data initialization completed successfully.");
            } catch (Exception e) {
                log.error("Error during data initialization: ", e);
            }
        };
    }
}
