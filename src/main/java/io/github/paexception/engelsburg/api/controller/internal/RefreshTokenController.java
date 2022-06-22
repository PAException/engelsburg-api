/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Controller for refresh tokens.
 */
@Component
@AllArgsConstructor
public class RefreshTokenController {

	private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * Create a refresh token by userId which lasts 30 days.
	 *
	 * @param user to create to
	 * @return created token
	 */
	public String create(UserModel user) {
		//Delete expired tokens
		List<RefreshTokenModel> refreshTokens = this.refreshTokenRepository.findAllByUser(user);
		refreshTokens.removeIf(rt -> rt.getExpire() < System.currentTimeMillis());
		this.refreshTokenRepository.deleteAll(refreshTokens);

		//Save and return token
		return this.refreshTokenRepository.save(
				new RefreshTokenModel(
						-1,
						user,
						RandomStringUtils.randomAlphanumeric(100),
						System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 60
				)
		).getToken();
	}

	/**
	 * Verify the refresh token.
	 *
	 * @param refreshToken to verify
	 * @return null if not found or expired, user if not
	 */
	public UserModel verifyRefreshToken(String refreshToken) {
		//Get token, if not present return null
		Optional<RefreshTokenModel> optionalRefreshToken = this.refreshTokenRepository.findByToken(refreshToken);
		if (optionalRefreshToken.isEmpty()) return null;

		//Return null if expired, user if valid
		UserModel user = optionalRefreshToken.get().getExpire() >= System.currentTimeMillis()
				? optionalRefreshToken.get().getUser()
				: null;
		this.refreshTokenRepository.delete(optionalRefreshToken.get());
		return user;
	}

	/**
	 * Deletes all refresh tokens of user.
	 *
	 * @param user of refresh tokens to delete
	 */
	public void deleteRefreshTokensOfUser(UserModel user) {
		this.refreshTokenRepository.deleteAllByUser(user);
	}
}
