package io.github.paexception.engelsburginfrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburginfrastructure")
public class EngelsburgInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngelsburgInfrastructureApplication.class, args);
    }

}
