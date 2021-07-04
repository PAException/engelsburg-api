package io.github.paexception.engelsburg.api.controller.userdata;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetUserDataResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetUserDataResponseDTOModel;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserDataController {

	private static final List<UserDataHandler> USER_HANDLERS = new ArrayList<>();
	private static final String[] PREFIXES_AND_SUFFIXES = new String[]{"Controller", "Endpoint", "DTO", "Model", "Response", "Request", "Service"};

	/**
	 * All user handlers have to be registered with this method
	 *
	 * @param userHandler instance of the user handler
	 */
	public static void registerUserHandler(UserDataHandler userHandler) {
		USER_HANDLERS.add(userHandler);
	}

	/**
	 * Return all data of a user
	 *
	 * @param jwt with userId
	 * @return all user data
	 */
	public Result<GetUserDataResponseDTO> getUserData(DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		List<GetUserDataResponseDTOModel> responseDTOs = new ArrayList<>();
		for (UserDataHandler userHandler : USER_HANDLERS) {
			Object[] data = userHandler.getUserData(userId);
			data = Arrays.stream(data).filter(o -> o != null && ((o instanceof Collection) && !((Collection<?>) o).isEmpty()))//Drop if object null or empty
					.collect(Collectors.toList()).toArray(Object[]::new);

			if (data.length > 0) responseDTOs.add(new GetUserDataResponseDTOModel(this.getNameKey(userHandler), data));
		}

		if (responseDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, "user_data");
		else return Result.of(new GetUserDataResponseDTO(userId, responseDTOs));
	}

	/**
	 * Delete all data of or referring to user
	 *
	 * @param jwt with userId
	 * @return empty result
	 */
	@Transactional
	public Result<?> deleteUserData(DecodedJWT jwt) {
		USER_HANDLERS.forEach(userHandler -> userHandler.deleteUserData(UUID.fromString(jwt.getSubject())));

		return Result.empty();
	}

	/**
	 * Parse nameKey of a userHandler by the className
	 *
	 * @param userHandler to parse
	 * @return parsed nameKey
	 */
	private String getNameKey(UserDataHandler userHandler) {
		String nameKey = userHandler.getClass().getSimpleName();
		for (String prefixOrSuffix : PREFIXES_AND_SUFFIXES) nameKey = nameKey.replaceAll(prefixOrSuffix, "");

		return nameKey;
	}

}
