package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * RestController for authentication actions
 */
@Validated
@RestController
public class AuthenticationEndpoint {

	@Autowired
	private AuthenticationController authenticationController;

	/**
	 * Login a user
	 *
	 * @see AuthenticationController#login(LoginRequestDTO)
	 */
	@PostMapping("/auth/login")
	public Object login(@RequestBody @Valid LoginRequestDTO dto) {
		return this.authenticationController.login(dto).getHttpResponse();
	}

	/**
	 * Signup a user
	 *
	 * @see AuthenticationController#signUp(SignUpRequestDTO)
	 */
	@PostMapping("/auth/signup")
	public Object signup(@RequestBody @Valid SignUpRequestDTO dto) {
		return this.authenticationController.signUp(dto).getHttpResponse();
	}

	/**
	 * Reset a password
	 *
	 * @see AuthenticationController#resetPassword(String)
	 */
	@PostMapping("/auth/reset_password")
	public Object resetPassword(@RequestParam @NotBlank String email) {
		return this.authenticationController.resetPassword(email).getHttpResponse();
	}

}
