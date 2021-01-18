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
public class EngelsburgAPI {

    @Getter private static final String serviceToken = System.getProperty("service.token");
    @Getter private static final Logger LOGGER = LoggerFactory.getLogger(EngelsburgAPI.class.getSimpleName());

    /**
     * Start SpringApplication
     * @param args given by command line
     */
    public static void main(String[] args) {
        SpringApplication.run(EngelsburgAPI.class, args);
    }

}
