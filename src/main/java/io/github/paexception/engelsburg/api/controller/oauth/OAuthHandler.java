/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class helps to handle oauth verifications.
 */
@Component
public abstract class OAuthHandler {

	private static final Set<OAuthHandler> HANDLERS = new HashSet<>();

	/**
	 * Whether a service is supported or not.
	 *
	 * @param name of the service.
	 * @return true if supported, false if not
	 */
	public static boolean supports(String name) {
		if (name == null || name.isEmpty()) return false;

		return HANDLERS.stream().anyMatch(handler -> handler.getName().equals(name));
	}

	/**
	 * Register all oauthControllers.
	 */
	@PostConstruct
	public final void registerOAuthController() {
		HANDLERS.add(this);
	}

	/**
	 * Handle request to sign up or log in with oauth.
	 *
	 * @param accessToken to access resources
	 * @return email of user or null
	 */
	@Nullable
	abstract public HandlerResult verifyOAuthLoginRequest(String accessToken);

	@Getter
	@Setter
	@AllArgsConstructor(staticName = "of")
	public static class HandlerResult {

		@NotNull
		private final String username;
		@NotNull
		private final String identification;
		private List<String> grantScopes;

	}

	/**
	 * Getter of all oauth handlers.
	 *
	 * @return all oauth handlers.
	 */
	public static Set<OAuthHandler> getHandlers() {
		return HANDLERS;
	}

	/**
	 * Get the name of the {@link OAuthHandler} to identify endpoints.
	 *
	 * @return the name
	 */
	@NotNull
	abstract public String getName();
}
