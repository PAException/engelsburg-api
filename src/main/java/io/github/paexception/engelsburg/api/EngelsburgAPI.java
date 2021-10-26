package io.github.paexception.engelsburg.api;

import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import lombok.Getter;
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
	private static final JwtUtil JWT_UTIL = new JwtUtil("engelsburg-api", Collections.emptyList(), Environment.JWT_SECRET);

	/**
	 * Start SpringApplication.
	 *
	 * @param args given by command line
	 */
	public static void main(String[] args) {
		SpringApplication.run(EngelsburgAPI.class, args);
	}

}
