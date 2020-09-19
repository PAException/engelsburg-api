package io.github.paexception.engelsburg.api;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburg.api")
public class EngelsburgAPIApplication {

    public static final String SERVICE_TOKEN = System.getenv("SERVICE_TOKEN");

    @Getter private static final Logger LOGGER = LoggerFactory.getLogger(EngelsburgAPIApplication.class.getSimpleName());

    public static void main(String[] args) {
        SpringApplication.run(EngelsburgAPIApplication.class, args);
    }

}
