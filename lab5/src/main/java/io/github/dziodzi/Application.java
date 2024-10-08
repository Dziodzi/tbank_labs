package io.github.dziodzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.dziodzi")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
