/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserOAuthModel;
import io.github.paexception.engelsburg.api.database.repository.user.UserOAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Controller to handle OAuth login methods of users.
 */
@Component
@AllArgsConstructor
public class UserOAuthController {

	private final UserOAuthRepository userOAuthRepository;

	/**
	 * Create a new OAuth method of user.
	 *
	 * @param user           to assign OAuth method to
	 * @param service        of method
	 * @param identification of method
	 * @param name           some email or else
	 */
	public void create(UserModel user, String service, String identification, String name) {
		this.userOAuthRepository.save(new UserOAuthModel(-1, user, service, identification, name));
	}

	/**
	 * Check of an OAuth method of a user exists.
	 *
	 * @param service        of method
	 * @param identification of method
	 * @return true if exists
	 */
	public boolean exists(String service, String identification) {
		return this.userOAuthRepository.existsByServiceAndIdentification(service, identification);
	}

	/**
	 * Get an OAuth method.
	 *
	 * @param service        of method
	 * @param identification of method
	 * @return Optional OAuth method
	 */
	public Optional<UserOAuthModel> get(String service, String identification) {
		return this.userOAuthRepository.findByServiceAndIdentification(service, identification);
	}

	/**
	 * Get an OAuth method by user and service.
	 *
	 * @param user    of method
	 * @param service of method
	 * @return Optional OAuth method
	 */
	public Optional<UserOAuthModel> getByUserAndService(UserModel user, String service) {
		return this.userOAuthRepository.findByUserAndService(user, service);
	}

	/**
	 * Get a string array of all OAuth methods with username used by a user.
	 *
	 * @param user of OAuth methods
	 * @return all methods
	 */
	public String[] getServicesByUser(UserModel user) {
		return this.userOAuthRepository.findAllByUser(user).stream()
				.map(oauth -> oauth.getService() + ":" + oauth.getName()).toArray(String[]::new);
	}

	/**
	 * Delete an OAuth method.
	 *
	 * @param userOAuthModel to delete
	 */
	public void delete(UserOAuthModel userOAuthModel) {
		this.userOAuthRepository.delete(userOAuthModel);
	}
}
