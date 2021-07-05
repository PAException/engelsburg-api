package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.repository.RefreshTokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for refresh tokens.
 */
@Component
public class RefreshTokenController {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	/**
	 * Create an refresh token by userId which lasts 30 days.
	 *
	 * @param userId to create to
	 * @return created token
	 */
	public String create(UUID userId) {
		Optional<RefreshTokenModel> optionalRefreshToken = this.refreshTokenRepository.findByUserId(userId);
		RefreshTokenModel refreshToken = optionalRefreshToken.orElseGet(() -> new RefreshTokenModel(userId));
		refreshToken.setToken(RandomStringUtils.randomAlphanumeric(100));
		refreshToken.setExpire(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30);

		return this.refreshTokenRepository.save(refreshToken).getToken();
	}

	/**
	 * Verify the refresh token.
	 *
	 * @param refreshToken to verify
	 * @return null if not found or expired, userId if not
	 */
	public UUID verifyRefreshToken(String refreshToken) {
		Optional<RefreshTokenModel> optionalRefreshToken = this.refreshTokenRepository.findByToken(refreshToken);
		if (optionalRefreshToken.isEmpty()) return null;

		return optionalRefreshToken.get().getExpire() > System.currentTimeMillis() ? optionalRefreshToken.get().getUserId() : null;
	}

}
