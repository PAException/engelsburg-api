/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Controller to handle google OAuth2.
 */
@Component
public class GoogleOAuth2Impl extends OAuthHandler {

	private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
			.Builder(new NetHttpTransport(), new GsonFactory()).build();

	/**
	 * Email of user is in JWT.
	 *
	 * @param accessToken to access resources
	 * @return email or null if any error occurred
	 */
	@Nullable
	@Override
	public HandlerResult verifyOAuthLoginRequest(String accessToken) {
		try {
			//Verify token
			GoogleIdToken idToken = this.verifier.verify(accessToken);

			//If idToken is valid return HandlerResult
			if (idToken != null)
				return HandlerResult.of(
						idToken.getPayload().getEmail(),
						idToken.getPayload().getSubject(),
						List.of()
				);
		} catch (Exception ignored) {
		}

		//Fail on any exception or if idToken is null
		return null;
	}

	@Override
	public String getName() {
		return "google";
	}
}
