/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.internal.UserController;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

/**
 * Controller to handle userData fetching or deleting of users.
 */
@Component
@AllArgsConstructor
public class UserDataController {

	private final UserController userController;

	/**
	 * Return all data of a user.
	 *
	 * @param userDTO user information
	 * @return all user data
	 */
	@Transactional
	public Result<?> getUserData(UserDTO userDTO) {
		//Workaround because of LazyInitializationException, transaction of userDTO.user is opened and closed in
		//AuthenticationInterceptor, lazy values cannot be accessed outside that transaction
		UserModel user = this.userController.get(userDTO.user.getUserId());

		return Result.of(user);
	}

	/**
	 * Delete all data of or referring to user.
	 *
	 * @param userDTO with userId
	 * @return empty result
	 */
	@Transactional
	public Result<?> deleteUserData(UserDTO userDTO) {
		//Delete user and return empty result
		this.userController.delete(userDTO.user);
		return Result.empty();
	}
}
