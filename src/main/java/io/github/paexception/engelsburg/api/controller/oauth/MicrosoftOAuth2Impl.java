/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.util.Environment;
import org.springframework.stereotype.Component;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Controller to handle microsoft OAuth2.
 */
@Component
public class MicrosoftOAuth2Impl extends OAuthHandler {

	/**
	 * Verifies the accessToken of a previous made Microsoft OAuth request.
	 *
	 * @param accessToken to access resources
	 * @return email or null if any error occurred
	 */
	@Nullable
	@Override
	public HandlerResult verifyOAuthLoginRequest(String accessToken) {
		try {
			//Decode accessToken as JWT
			DecodedJWT jwt = JWT.decode(accessToken);

			//Get public key as "kid" in jwt header
			byte[] keyBytes = Base64.getDecoder().decode(
					jwt.getHeaderClaim("kid").asString().getBytes(StandardCharsets.UTF_8));
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(spec);

			//Verify jwt
			jwt = JWT.require(Algorithm.RSA256(key, null)).withAudience(Environment.MICROSOFT_APP_ID)
					.build().verify(accessToken);
			if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(Date.from(Instant.now()))) return null;

			//Return HandlerResult
			String email = jwt.getClaim("preferred_username").asString();
			List<String> scopes = new ArrayList<>();
			//Grant several scopes if email is associated to a member of the school (student or teacher)
			if (email.endsWith("@smmp-eb.de") || email.endsWith("@smmp-eb.schule")) {
				scopes.add("substitute.read.current");
				scopes.add("substitute.message.read.current");
				scopes.add("info.teacher.read.all");
				scopes.add("info.classes.read.all");
			}

			return HandlerResult.of(
					email,
					jwt.getClaim("sub").asString(),
					scopes
			);
		} catch (Exception ignored) {
		}

		//Fail on any exception or if JWT verification fails
		return null;
	}

	@Override
	public String getName() {
		return "microsoft";
	}
}
