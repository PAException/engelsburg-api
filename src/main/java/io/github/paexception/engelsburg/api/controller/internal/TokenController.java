package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.TokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Controller for tokens like verify or reset password tokens.
 */
@Component
public class TokenController implements UserDataHandler {

	private final TokenRepository tokenRepository;

	public TokenController(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	/**
	 * Creates a new token.
	 *
	 * @param user  of token
	 * @param type  of token
	 * @param token itself
	 */
	public void createToken(UserModel user, String type, String token) {
		this.tokenRepository.save(new TokenModel(-1, user, type, token, -1));
	}

	/**
	 * Creates a random alphanumeric token with a length of 8.
	 *
	 * @param user of token
	 * @param type of token
	 * @return the random generated token
	 */
	public String createRandomToken(UserModel user, String type) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createToken(user, type, random);

		return random;
	}

	/**
	 * Creates a new temporary token.
	 *
	 * @param user  of token
	 * @param type  of token
	 * @param token itself
	 * @param exp   millis
	 */
	public void createTemporaryToken(UserModel user, String type, String token, long exp) {
		this.tokenRepository.save(new TokenModel(-1, user, type, token, exp));
	}

	/**
	 * Creates a temporary random alphanumeric token with a length of 8.
	 *
	 * @param user of token
	 * @param type of token
	 * @param exp  millis
	 * @return the random generated token
	 */
	public String createRandomTemporaryToken(UserModel user, String type, long exp) {
		String random = RandomStringUtils.randomAlphanumeric(8);
		this.createTemporaryToken(user, type, random, exp);

		return random;
	}

	/**
	 * Checks if a token exists and is not expired.
	 * If expired deletes the token.
	 *
	 * @param user  of token
	 * @param type  of token
	 * @param token itself
	 * @return if token exists
	 */
	public boolean checkToken(UserModel user, String type, String token) {
		Optional<TokenModel> optionalToken = this.tokenRepository.findByUserAndTypeAndToken(user, type, token);
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
	 * Deletes a token.
	 *
	 * @param user  of token
	 * @param type  of token
	 * @param token itself
	 */
	@Transactional
	public void deleteToken(UserModel user, String type, String token) {
		this.tokenRepository.deleteByUserAndTypeAndToken(user, type, token);
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.tokenRepository.deleteByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.tokenRepository.findAllByUser(user));
	}

}
