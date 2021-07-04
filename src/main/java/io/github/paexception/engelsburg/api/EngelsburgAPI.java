package io.github.paexception.engelsburg.api;

import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.Collections;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "io.github.paexception.engelsburg.api")
public class EngelsburgAPI {

	@Getter
	private static final JwtUtil jwtUtil = new JwtUtil("engelsburg-api", Collections.emptyList(), Environment.JWT_SECRET);
	@Getter
	private static final Logger LOGGER = LoggerFactory.getLogger(EngelsburgAPI.class.getSimpleName());

	/**
	 * Start SpringApplication
	 *
	 * @param args given by command line
	 */
	public static void main(String[] args) {
		SpringApplication.run(EngelsburgAPI.class, args);
	}

}
