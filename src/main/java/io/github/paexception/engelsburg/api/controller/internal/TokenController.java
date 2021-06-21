package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.repository.TokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for tokens like verify or reset password tokens
 */
@Component
public class TokenController implements UserDataHandler {

	@Autowired
	private TokenRepository tokenRepository;

	/**
	 * Creates a new token
	 *
	 * @param userId of token
	 * @param type   of token
	 * @param token  itself
	 */
	public void createToken(UUID userId, String type, String token) {
		this.tokenRepository.save(new TokenModel(-1, userId, type, token, -1));
	}

	/**
	 * Creates a random alphanumeric token with a length of 8
	 *
	 * @param userId of token
	 * @param type   of token
	 * @return the random generated token
	 */
	public String createRandomToken(UUID userId, String type) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createToken(userId, type, random);

		return random;
	}

	/**
	 * Creates a new temporary token
	 *
	 * @param userId of token
	 * @param type   of token
	 * @param token  itself
	 * @param exp    millis
	 */
	public void createTemporaryToken(UUID userId, String type, String token, long exp) {
		this.tokenRepository.save(new TokenModel(-1, userId, type, token, exp));
	}

	/**
	 * Creates a temporary random alphanumeric token with a length of 8
	 *
	 * @param userId of token
	 * @param type   of token
	 * @param exp    millis
	 * @return the random generated token
	 */
	public String createRandomTemporaryToken(UUID userId, String type, long exp) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createTemporaryToken(userId, type, random, exp);

		return random;
	}

	/**
	 * Checks if a token exists and is not expired.
	 * If expired deletes the token.
	 *
	 * @param userId of token
	 * @param type   of token
	 * @param token  itself
	 * @return if token exists
	 */
	public boolean checkToken(UUID userId, String type, String token) {
		Optional<TokenModel> optionalToken = this.tokenRepository.findByUserIdAndTypeAndToken(userId, type, token);
		if (optionalToken.isPresent()) {
			long exp = optionalToken.get().getExp();
			if (exp < 0) return true;
			if (exp >= System.currentTimeMillis()) return true;
			else {
				this.tokenRepository.delete(optionalToken.get());
				return false;
			}
		} else return false;
	}

	/**
	 * Deletes a token
	 *
	 * @param userId of token
	 * @param type   of token
	 * @param token  itself
	 */
	public void deleteToken(UUID userId, String type, String token) {
		this.tokenRepository.deleteByUserIdAndTypeAndToken(userId, type, token);
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.tokenRepository.deleteByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.tokenRepository.findAllByUserId(userId));
	}

}
