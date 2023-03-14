/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserPasswordModel;
import io.github.paexception.engelsburg.api.database.repository.user.UserPasswordRepository;
import io.github.paexception.engelsburg.api.util.Hash;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;

/**
 * Controller to handle password login methods of users.
 */
@Component
@AllArgsConstructor
public class UserPasswordController {

	private final UserPasswordRepository userPasswordRepository;

	/**
	 * Verify if the given password matches the saved one of a user.
	 *
	 * @param userPassword to check against
	 * @param password     to validate
	 * @return true if it matches
	 */
	public static boolean verifyPasswort(@NotNull UserPasswordModel userPassword, String password) {
		return Arrays.equals(userPassword.getPassword(), Hash.sha256(password + userPassword.getSalt()));
	}

	/**
	 * Create a new password of a user.
	 *
	 * @param user     to create password to
	 * @param email    to create password to
	 * @param password password
	 */
	public void create(@NotNull UserModel user, String email, String password) {
		String salt = RandomStringUtils.randomAlphanumeric(16);
		byte[] hashedPassword = Hash.sha256(password + salt);

		this.userPasswordRepository.save(new UserPasswordModel(-1, user, email, hashedPassword, salt));
	}

	/**
	 * Check if a password login method is existing of an email.
	 *
	 * @param email of password login
	 * @return if existing
	 */
	public boolean existsByEmail(String email) {
		return this.userPasswordRepository.existsByEmail(email);
	}

	/**
	 * Update the current password of a user.
	 *
	 * @param userPassword to identify user
	 * @param password     New to set
	 */
	public void updatePassword(@NotNull UserPasswordModel userPassword, String password) {
		userPassword.setPassword(Hash.sha256(password + userPassword.getSalt()));
		this.userPasswordRepository.save(userPassword);
	}

	/**
	 * Get a password login method of a user by an email.
	 *
	 * @param email to get password login method
	 * @return Optional password login method
	 */
	public Optional<UserPasswordModel> getByEmail(String email) {
		return this.userPasswordRepository.findByEmail(email);
	}


	/**
	 * Get a password login method of a user by the user.
	 *
	 * @param user to get password login method
	 * @return Optional password login method
	 */
	public Optional<UserPasswordModel> get(UserModel user) {
		return user == null ? Optional.empty() : this.userPasswordRepository.findByUser(user);
	}
}
