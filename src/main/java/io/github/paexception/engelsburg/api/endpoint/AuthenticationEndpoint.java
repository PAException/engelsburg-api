/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimit;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimiter;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.Map;

/**
 * RestController for authentication actions.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationEndpoint extends RateLimiter {

	private final AuthenticationController authenticationController;

	public AuthenticationEndpoint(AuthenticationController authenticationController) {
		super(Bucket.builder().addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)))));
		this.authenticationController = authenticationController;
	}

	/**
	 * Signup a user.
	 *
	 * @see AuthenticationController#signUp(SignUpRequestDTO)
	 */
	@RateLimit
	@PostMapping("/signup")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "user")
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "signup")
	public Object signup(@RequestBody @Valid SignUpRequestDTO dto) {
		return this.authenticationController.signUp(dto).getHttpResponse();
	}

	/**
	 * Login a user.
	 *
	 * @see AuthenticationController#login(LoginRequestDTO)
	 */
	@RateLimit
	@PostMapping("/login")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "user")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "wrong_password")
	public Object login(@RequestBody @Valid LoginRequestDTO dto) {
		return this.authenticationController.login(dto).getHttpResponse();
	}

	/**
	 * Authenticate user.
	 *
	 * @see AuthenticationController#auth(String)
	 */
	@GetMapping("/refresh")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "refresh_token")
	public Object authenticate(
			@RequestParam @NotBlank @Schema(example = "vUt1a1SiqDOnjCt5NqiflIgCGrOUTR5xXqdbWhh4fXHHKUoqW5bQiw5UGNCH1NbxyguI8rrgK09Me9LSS341NBmOMSsBgThpJFeu") String refreshToken) {
		return this.authenticationController.auth(refreshToken).getHttpResponse();
	}

	/**
	 * Request to reset a password.
	 *
	 * @see AuthenticationController#requestResetPassword(String, UserDTO)
	 */
	@RateLimit
	@PostMapping("/request_reset_password")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "user")
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "request_reset_password")
	public Object requestResetPassword(@RequestParam @NotBlank @Schema(example = "any.email@gmail.com") String email,
			UserDTO userDTO) {
		return this.authenticationController.requestResetPassword(email, userDTO).getHttpResponse();
	}

	/**
	 * Reset a password.
	 *
	 * @see AuthenticationController#resetPassword(ResetPasswordRequestDTO)
	 */
	@RateLimit
	@PatchMapping("/reset_password")
	@Response
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "reset_password")
	public Object resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
		return this.authenticationController.resetPassword(dto).getHttpResponse();
	}

	/**
	 * Verify a user.
	 *
	 * @see AuthenticationController#verify(String)
	 */
	@RateLimit
	@PatchMapping("/verify")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "verify")
	public Object verify(@RequestParam @NotBlank @Schema(example = "B2JGa74N") String token) {
		return this.authenticationController.verify(token).getHttpResponse();
	}

	/**
	 * Request a scope to grant to a user.
	 *
	 * @see AuthenticationController#requestScopes(Map, UserDTO)
	 */
	@RateLimit
	@AuthScope
	@PatchMapping("/scope")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "failed.scope.1,...")
	public Object requestScopes(
			@RequestParam @Schema(example = "school_token", description = "Can be any scope e.g. substitute.read.current") Map<String, String> requestParams,
			UserDTO userDTO) {
		return this.authenticationController.requestScopes(requestParams, userDTO).getHttpResponse();
	}
}
