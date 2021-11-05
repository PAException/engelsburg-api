package io.github.paexception.engelsburg.api.controller.userdata;

import io.github.paexception.engelsburg.api.database.model.UserModel;
import javax.annotation.PostConstruct;

/**
 * This interface has to be implemented by all classes which save userIds.
 */
public interface UserDataHandler {

	/**
	 * Deletes or masks the given userId everywhere.
	 *
	 * @param user which has to deleted or masked everywhere
	 */
	void deleteUserData(UserModel user);

	/**
	 * Collects all data where the userId is used and returns it in json format.
	 *
	 * @param user after what the data is collected
	 * @return a String of collected data in json format
	 */
	Object[] getUserData(UserModel user);

	/**
	 * Help to map data for {@link UserDataHandler#getUserData(UserModel)}.
	 *
	 * @param data to map
	 * @return mapped Object[]
	 */
	default Object[] mapData(Object... data) {
		return data;
	}

	/**
	 * Registers the class implementing <i>UserHandler</i> in <i>UserController</i>.
	 * <b>Please do not override this method</b>
	 *
	 * @see UserDataController
	 */
	@PostConstruct
	default void register() {
		UserDataController.registerUserHandler(this);
	}

}
