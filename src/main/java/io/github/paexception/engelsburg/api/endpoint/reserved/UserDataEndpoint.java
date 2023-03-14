/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.UserDataController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for user data actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user/data")
public class UserDataEndpoint {

	private final UserDataController userDataController;

	/**
	 * Get data of user.
	 *
	 * @see UserDataController#getUserData(UserDTO)
	 */
	@AuthScope("user.data.read.self")
	@GetMapping
	@Response(value = Object.class, description = "This could be any data related to the user")
	public Object getData(UserDTO userDTO) {
		return this.userDataController.getUserData(userDTO).getHttpResponse();
	}

	/**
	 * Delete all data of user.
	 *
	 * <b>The whole account will be deleted and there is no way of restoring</b>
	 *
	 * @see UserDataController#deleteUserData(UserDTO)
	 */
	@AuthScope("user.data.delete.self")
	@DeleteMapping
	@Response
	public Object deleteData(UserDTO userDTO) {
		return this.userDataController.deleteUserData(userDTO).getHttpResponse();
	}
}
