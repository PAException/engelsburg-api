package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for user data actions.
 */
@Validated
@RestController
public class UserDataEndpoint {

	private final UserDataController userDataController;

	public UserDataEndpoint(UserDataController userDataController) {
		this.userDataController = userDataController;
	}

	/**
	 * Get data of user.
	 *
	 * @see UserDataController#getUserData(UserDTO)
	 */
	@AuthScope("user.data.read.self")
	@GetMapping("/user/data")
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
	@DeleteMapping("/user/data")
	public Object deleteData(UserDTO userDTO) {
		return this.userDataController.deleteUserData(userDTO).getHttpResponse();
	}

}
