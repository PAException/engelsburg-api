package io.github.paexception.engelsburg.api.controller.userdata;

import io.github.paexception.engelsburg.api.util.Result;
import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * This interface has to be implemented by all classes which save userIds.
 */
public interface UserDataHandler {

	/**
	 * Deletes or masks the given userId everywhere.
	 *
	 * @param userId the userId which has to deleted or masked everywhere
	 */
	void deleteUserData(UUID userId);

	/**
	 * Collects all data where the userId is used and returns it in json format.
	 *
	 * @param userId the userId after what the data is collected
	 * @return a String of collected data in json format
	 */
	Result<?> getUserData(UUID userId);

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
