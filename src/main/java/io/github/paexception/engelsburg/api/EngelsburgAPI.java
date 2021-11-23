package io.github.paexception.engelsburg.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburg.api")
public class EngelsburgAPI {

	/**
	 * Start Engelsburg-Api application.
	 *
	 * @param args given by command line
	 */
	public static void main(String[] args) {
		SpringApplication.run(EngelsburgAPI.class, args);
	}

}
