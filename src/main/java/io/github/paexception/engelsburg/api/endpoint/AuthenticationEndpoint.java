package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * RestController for authentication actions.
 */
@Validated
@RestController
public class AuthenticationEndpoint {

	@Autowired
	private AuthenticationController authenticationController;

	/**
	 * Signup a user.
	 *
	 * @see AuthenticationController#signUp(SignUpRequestDTO)
	 */
	@PostMapping("/auth/signup")
	public Object signup(@RequestBody @Valid SignUpRequestDTO dto) {
		return this.authenticationController.signUp(dto).getHttpResponse();
	}

	/**
	 * Login a user.
	 *
	 * @see AuthenticationController#login(LoginRequestDTO)
	 */
	@PostMapping("/auth/login")
	public Object login(@RequestBody @Valid LoginRequestDTO dto) {
		return this.authenticationController.login(dto).getHttpResponse();
	}

	/**
	 * Authenticate user.
	 *
	 * @see AuthenticationController#auth(String)
	 */
	@GetMapping("/auth/refresh")
	public Object auth(@RequestParam @NotBlank String refreshToken) {
		return this.authenticationController.auth(refreshToken).getHttpResponse();
	}

	/**
	 * Request to reset a password.
	 *
	 * @see AuthenticationController#requestResetPassword(String)
	 */
	@PostMapping("/auth/request_reset_password")
	public Object requestResetPassword(@RequestParam @NotBlank String email) {
		return this.authenticationController.requestResetPassword(email).getHttpResponse();
	}

	/**
	 * Reset a password.
	 *
	 * @see AuthenticationController#resetPassword(ResetPasswordRequestDTO)
	 */
	@PatchMapping("/auth/reset_password")
	public Object resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
		return this.authenticationController.resetPassword(dto).getHttpResponse();
	}

	/**
	 * Verify a user.
	 *
	 * @see AuthenticationController#verify(DecodedJWT, String)
	 */
	@AuthScope
	@PatchMapping("/auth/verify/{token}")
	public Object verify(DecodedJWT jwt, @PathVariable @NotBlank String token) {
		return this.authenticationController.verify(jwt, token).getHttpResponse();
	}

}
