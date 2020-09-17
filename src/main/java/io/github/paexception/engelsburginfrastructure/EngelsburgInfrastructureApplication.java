package io.github.paexception.engelsburginfrastructure;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburginfrastructure")
public class EngelsburgInfrastructureApplication {

    @Getter private static final Logger LOGGER = LoggerFactory.getLogger(EngelsburgInfrastructureApplication.class.getSimpleName());

    public static void main(String[] args) {
        SpringApplication.run(EngelsburgInfrastructureApplication.class, args);
    }

}
