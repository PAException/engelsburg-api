/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.paexception.engelsburg.api.controller.oauth.OAuthController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimit;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimiter;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;
import java.time.Duration;

/**
 * RestController for oauth actions.
 */
@RestController
@RequestMapping("/auth/oauth")
public class OAuthEndpoint extends RateLimiter {

	private final OAuthController oAuthController;

	public OAuthEndpoint(OAuthController oAuthController) {
		super(Bucket.builder().addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)))));
		this.oAuthController = oAuthController;
	}

	/**
	 * Request oauth signup.
	 *
	 * @see OAuthController#signUp(String, String)
	 */
	@RateLimit
	@PostMapping("/{service}")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "oauth")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "oauth_service")
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "user")
	public Object signUp(@PathVariable @NotBlank @Schema(example = "google") String service,
			@RequestParam @NotBlank @Schema(example = "1/fFAGRNJru1FTz70BzhT3Zg") String accessToken) {
		return this.oAuthController.signUp(service, accessToken).getHttpResponse();
	}

	/**
	 * Request oauth login.
	 *
	 * @see OAuthController#login(String, String)
	 */
	@RateLimit
	@GetMapping("/{service}")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "oauth")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "oauth_service", key = "OAuth service")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "user", key = "User")
	public Object login(@PathVariable @NotBlank @Schema(example = "google") String service,
			@RequestParam @NotBlank @Schema(example = "1/fFAGRNJru1FTz70BzhT3Zg") String accessToken) {
		return this.oAuthController.login(service, accessToken).getHttpResponse();
	}

	/**
	 * Request to connect oauth to existing account.
	 *
	 * @see OAuthController#connectToAccount(String, String, UserDTO)
	 */
	@RateLimit
	@AuthScope
	@PatchMapping("/{service}")
	@Response(AuthResponseDTO.class)
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "oauth")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "oauth_service")
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "oauth")
	public Object connectToAccount(@PathVariable @NotBlank @Schema(example = "google") String service,
			@RequestParam @NotBlank @Schema(example = "1/fFAGRNJru1FTz70BzhT3Zg") String accessToken,
			UserDTO userDTO) {
		return this.oAuthController.connectToAccount(service, accessToken, userDTO).getHttpResponse();
	}

	/**
	 * Request to disconnect oauth from account.
	 *
	 * @see OAuthController#disconnectFromAccount(String, UserDTO)
	 */
	@RateLimit
	@AuthScope
	@DeleteMapping("/{service}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "oauth_service", key = "OAuth service")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "oauth", key = "OAuth")
	@ErrorResponse(status = 400, messageKey = "FAILED", extra = "oauth")
	public Object disconnectFromAccount(@PathVariable @NotBlank @Schema(example = "google") String service,
			UserDTO userDTO) {
		return this.oAuthController.disconnectFromAccount(service, userDTO).getHttpResponse();
	}
}
