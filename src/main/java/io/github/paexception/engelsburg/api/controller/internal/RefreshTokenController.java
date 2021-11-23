package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.RefreshTokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Controller for refresh tokens.
 */
@Component
public class RefreshTokenController {

	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshTokenController(
			RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}

	/**
	 * Create a refresh token by userId which lasts 30 days.
	 *
	 * @param user to create to
	 * @return created token
	 */
	public String create(UserModel user) {
		Optional<RefreshTokenModel> optionalRefreshToken = this.refreshTokenRepository.findByUser(user);
		RefreshTokenModel refreshToken = optionalRefreshToken.orElseGet(() -> new RefreshTokenModel(user));
		refreshToken.setToken(RandomStringUtils.randomAlphanumeric(100));
		refreshToken.setExpire(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 60);

		return this.refreshTokenRepository.save(refreshToken).getToken();
	}

	/**
	 * Verify the refresh token.
	 *
	 * @param refreshToken to verify
	 * @return null if not found or expired, user if not
	 */
	public UserModel verifyRefreshToken(String refreshToken) {
		Optional<RefreshTokenModel> optionalRefreshToken = this.refreshTokenRepository.findByToken(refreshToken);
		if (optionalRefreshToken.isEmpty()) return null;

		return optionalRefreshToken.get().getExpire() >= System.currentTimeMillis() ? optionalRefreshToken.get().getUser() : null;
	}

}
