/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Class to create and handle JWT's.
 * Contains a singleton implementation.
 */
public class JwtUtil {

	private static JwtUtil instance;
	private final String issuer;
	private final List<String> defaultAudience;
	private final Algorithm algorithm;
	private final JWTVerifier verifier;

	/**
	 * Constructor.
	 *
	 * @param issuer          the organization/person/domain issuing the JWTs
	 * @param defaultAudience the default audience for every JWT
	 * @param secret          the secret used to sign the JWT
	 */
	public JwtUtil(String issuer, List<String> defaultAudience, String secret) {
		this.issuer = issuer;
		this.defaultAudience = defaultAudience;

		this.algorithm = Algorithm.HMAC512(secret);
		this.verifier = JWT.require(this.algorithm)
				.withIssuer(this.issuer)
				.withAudience(this.defaultAudience.toArray(String[]::new))
				.build();
	}

	/**
	 * Verifies the given <i>token</i> and returns a pair including the decoded token and the result.
	 *
	 * @param token the raw json web token
	 * @return a pair including the decoded token and the result
	 */
	public Pair<DecodedJWT, VerificationResult> verify(String token) {
		if (token.toLowerCase().startsWith("bearer")) token = token.substring(7);

		DecodedJWT jwt;

		// Check whether the token is even valid
		try {
			jwt = JWT.decode(token);
		} catch (JWTDecodeException ex) {
			return Pair.of(null, VerificationResult.INVALID);
		} catch (Exception ex) {
			return Pair.of(null, VerificationResult.UNKNOWN);
		}

		if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(Date.from(Instant.now())))
			return Pair.of(jwt, VerificationResult.EXPIRED);

		try {
			this.verifier.verify(jwt);
		} catch (JWTVerificationException ex) {
			return Pair.of(jwt, VerificationResult.FAILED);
		} catch (Exception ex) {
			return Pair.of(null, VerificationResult.UNKNOWN);
		}

		return Pair.of(jwt, VerificationResult.SUCCESS);
	}

	public String sign(JWTCreator.Builder builder) {
		return builder.sign(this.algorithm);
	}

	/**
	 * Creates a default jwt which can be used as a CSRF or refresh token - or whatever u want.
	 *
	 * @param subject            the subject (user) of this token
	 * @param expirationTime     the amount of <i>expirationTimeUnit</i> the token shall be valid
	 * @param expirationTimeUnit the time unit used for <i>expirationTime</i>; Use Calendar.XY for this
	 * @param audience           the audience the token is meant for
	 * @return a jwt containing default values
	 */
	public String create(String subject, int expirationTime, int expirationTimeUnit, String... audience) {
		return this.sign(this.createBuilder(subject, expirationTime, expirationTimeUnit, audience));
	}

	/**
	 * Creates a default jwt which can be used as a CSRF or refresh token - or whatever u want.
	 *
	 * <p>
	 * To be compliant with most standards, you should set the subject (the user).
	 * </p>
	 *
	 * @param expirationTime     the amount of <i>expirationTimeUnit</i> the token shall be valid
	 * @param expirationTimeUnit the time unit used for <i>expirationTime</i>; Use Calendar.XY for this
	 * @param audience           the audience the token is meant for
	 * @return a jwt containing default values
	 */
	public String create(int expirationTime, int expirationTimeUnit, String... audience) {
		return this.sign(this.createBuilder(expirationTime, expirationTimeUnit, audience));
	}

	/**
	 * Creates a default jwt which can be used as a CSRF or refresh token - or whatever u want.
	 *
	 * @param subject            the subject (user) of this token
	 * @param expirationTime     the amount of <i>expirationTimeUnit</i> the token shall be valid
	 * @param expirationTimeUnit the time unit used for <i>expirationTime</i>; Use Calendar.XY for this
	 * @param audience           the audience the token is meant for
	 * @return a {@link com.auth0.jwt.JWTCreator.Builder} instance containing default values
	 */
	public JWTCreator.Builder createBuilder(String subject, int expirationTime, int expirationTimeUnit,
			String... audience) {
		return this.createBuilder(expirationTime, expirationTimeUnit, audience).withSubject(subject);
	}

	/**
	 * Creates a default jwt which can be used as a CSRF or refresh token - or whatever u want.
	 *
	 * <p>
	 * To be compliant with most standards, you should set the subject (the user).
	 * </p>
	 *
	 * @param expirationTime     the amount of <i>expirationTimeUnit</i> the token shall be valid
	 * @param expirationTimeUnit the time unit used for <i>expirationTime</i>; Use Calendar.XY for this
	 * @param audience           the audience the token is meant for
	 * @return a {@link com.auth0.jwt.JWTCreator.Builder} instance containing default values
	 */
	public JWTCreator.Builder createBuilder(int expirationTime, int expirationTimeUnit, String... audience) {
		Date today = Date.from(Instant.now());

		Calendar expirationDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		expirationDate.setTime(today);
		expirationDate.add(expirationTimeUnit, expirationTime);

		// Audiences
		List<String> audienceList = new ArrayList<>(Arrays.asList(audience));
		audienceList.addAll(this.defaultAudience);

		return JWT.create()
				.withIssuer(this.issuer)
				.withAudience(audienceList.toArray(String[]::new))
				.withIssuedAt(today)
				.withExpiresAt(expirationDate.getTime());
	}

	public enum VerificationResult {
		SUCCESS,
		FAILED,
		INVALID,
		EXPIRED,
		UNKNOWN
	}

	/**
	 * Singleton implementation.
	 *
	 * @return jwtUtil instance
	 */
	public static JwtUtil getInstance() {
		if (instance == null) {
			instance = new JwtUtil("engelsburg-api", Collections.emptyList(), Environment.JWT_SECRET);
		}

		return instance;
	}

}
