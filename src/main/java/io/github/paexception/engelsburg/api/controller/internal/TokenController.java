/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;

/**
 * Controller for tokens like verify or reset password tokens.
 */
@Component
@AllArgsConstructor
public class TokenController {

	private final TokenRepository tokenRepository;

	/**
	 * Appends params on value.
	 * A comma after a value is always required because of vulnerability issues. (LIKE statement of query)
	 *
	 * <p>e.g. {@code value,}, {@code value,param1,param2}</p>
	 *
	 * @param value  to append params on
	 * @param params to append
	 * @return value with appended params
	 */
	private static String appendParams(String value, String... params) {
		StringBuilder builder = new StringBuilder(value + ",");
		for (String param : params) builder.append(param).append(",");

		//Delete last and return
		return builder.deleteCharAt(builder.length() - 1).toString();
	}

	/**
	 * Creates a new token.
	 *
	 * @param user   of token
	 * @param type   of token
	 * @param token  itself
	 * @param params to append
	 */
	public void createToken(UserModel user, String type, String token, String... params) {
		if (token.contains(",")) throw new IllegalArgumentException("',' is a reserved Character.");
		if (this.tokenRepository.existsByTokenWithParams(token)) return;

		this.tokenRepository.save(new TokenModel(-1, user, type, appendParams(token, params), -1));
	}

	/**
	 * Creates a random alphanumeric token with a length of 8.
	 *
	 * @param user   of token
	 * @param type   of token
	 * @param params optional params
	 * @return the random generated token
	 */
	public String createRandomToken(UserModel user, String type, String... params) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createToken(user, type, random, params);

		return random;
	}

	/**
	 * Creates a new temporary token.
	 *
	 * @param user   of token
	 * @param type   of token
	 * @param token  itself
	 * @param exp    millis
	 * @param params optional params
	 */
	public void createTemporaryToken(UserModel user, String type, String token, long exp, String... params) {
		if (token.contains(",")) throw new IllegalArgumentException("',' is a reserved Character.");
		if (this.tokenRepository.existsByTokenWithParams(token)) return;

		this.tokenRepository.save(new TokenModel(-1, user, type, appendParams(token, params), exp));
	}

	/**
	 * Creates a temporary random alphanumeric token with a length of 8.
	 *
	 * @param user   of token
	 * @param type   of token
	 * @param exp    millis
	 * @param params optional params
	 * @return the random generated token
	 */
	public String createRandomTemporaryToken(UserModel user, String type, long exp, String... params) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createTemporaryToken(user, type, random, exp, params);

		return random;
	}

	/**
	 * Checks if a token exists and is not expired.
	 * If expired deletes the token.
	 *
	 * @param type  of token
	 * @param token itself
	 * @return if token exists
	 */
	public boolean checkToken(String type, String token) {
		//Get token, if not present return false
		Optional<TokenModel> optionalToken = this.tokenRepository.findByTypeAndTokenWithParams(type, token);
		if (optionalToken.isPresent()) {
			long exp = optionalToken.get().getExp();

			//If token is not expired return true, otherwise delete token and return false
			if (exp == -1 || exp >= System.currentTimeMillis()) return true;
			else this.tokenRepository.delete(optionalToken.get());
		}

		return false;
	}

	/**
	 * Get params of token.
	 *
	 * @param type  of token
	 * @param token itself
	 * @return params
	 */
	public String[] getParams(String type, String token) {
		Optional<TokenModel> optionalToken = this.tokenRepository.findByTypeAndTokenWithParams(type, token);
		if (optionalToken.isEmpty()) return new String[0];

		String[] split = optionalToken.get().getToken().split(",");
		if (split.length == 1) return new String[0];

		return Arrays.copyOfRange(split, 1, split.length);
	}

	/**
	 * Deletes a token.
	 *
	 * @param user  of token
	 * @param type  of token
	 * @param token itself
	 */
	@Transactional
	public void deleteToken(UserModel user, String type, String token) {
		this.tokenRepository.deleteByUserAndTypeAndTokenWithParams(user, type, token);
	}
}
