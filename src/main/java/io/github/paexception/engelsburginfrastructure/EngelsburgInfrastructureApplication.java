package io.github.paexception.engelsburginfrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburginfrastructure")
public class EngelsburgInfrastructureApplication {

    public static final String SUBSTITUTE_PLAN_PASSWORD = System.getenv("SUBSTITUTE_PLAN_PASSWORD");

    public static void main(String[] args) {
        SpringApplication.run(EngelsburgInfrastructureApplication.class, args);
    }

}
