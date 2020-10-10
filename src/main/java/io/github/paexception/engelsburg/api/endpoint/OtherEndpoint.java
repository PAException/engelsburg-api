package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.spring.interceptor.IgnoreServiceToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@IgnoreServiceToken
@RestController
public class OtherEndpoint {

	@GetMapping("/ping")
	public Object pong() {
		return "pong";
	}

	@GetMapping("/")
	public Object index() {
		return standardInfo();
	}

	@GetMapping("/welcome")
	public Object welcome() {
		return standardInfo();
	}

	private String standardInfo() {
		return "{\n\t\"Hello\": \"there\"\n}";
	}

}
