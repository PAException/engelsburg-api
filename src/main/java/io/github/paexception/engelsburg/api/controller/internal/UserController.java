/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Controller to manage users.
 */
@Component
@AllArgsConstructor
public class UserController {

	private final UserRepository userRepository;

	/**
	 * Create a new user with a random UUID.
	 *
	 * @param username of user
	 * @param verified If user is verified
	 * @return created user
	 */
	public UserModel create(@NotNull String username, boolean verified) {
		return this.userRepository.save(new UserModel(-1, UUID.randomUUID(), verified, username));
	}

	/**
	 * Get a user by its userId.
	 *
	 * @param userId to get user
	 * @return user
	 */
	@Nullable
	public UserModel get(UUID userId) {
		return this.userRepository.findByUserId(userId);
	}

	/**
	 * Verify an existing user.
	 *
	 * @param user to verify
	 */
	public void verify(@NotNull UserModel user) {
		user.setVerified(true);
		this.userRepository.save(user);
	}

	/**
	 * Delete an existing user.
	 *
	 * @param user to delete.
	 */
	public void delete(UserModel user) {
		if (user == null) return;
		this.userRepository.delete(user);
	}

	public void updateCurrentSemester(UserModel user, SemesterModel semester) {
		user.setCurrentSemester(semester);
		this.userRepository.save(user);
	}

	/**
	 * Get all existing users.
	 *
	 * @return users
	 */
	public List<UserModel> getAll() {
		return this.userRepository.findAll();
	}

	/**
	 * Get all existing and verified users.
	 *
	 * @return verified users
	 */
	public List<UserModel> getAllVerified() {
		return this.userRepository.findAllByVerified(true);
	}
}
