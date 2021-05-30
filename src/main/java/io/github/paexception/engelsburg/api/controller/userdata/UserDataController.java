package io.github.paexception.engelsburg.api.controller.userdata;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetUserDataResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetUserDataResponseDTOModel;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserDataController {

	private static final List<UserDataHandler> USER_HANDLERS = new ArrayList<>();
	private static final String[] PREFIXES_AND_SUFFIXES = new String[]{"Controller", "Endpoint", "DTO", "Model", "Response", "Request"};

	public static void registerUserHandler(UserDataHandler userHandler) {
		USER_HANDLERS.add(userHandler);
	}

	public Result<GetUserDataResponseDTO> getUserData(DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		List<GetUserDataResponseDTOModel> responseDTOs = new ArrayList<>();
		for (UserDataHandler userHandler : USER_HANDLERS)
			responseDTOs.add(new GetUserDataResponseDTOModel(this.getNameKey(userHandler), userHandler.getUserData(userId)));

		return Result.of(new GetUserDataResponseDTO(userId, responseDTOs));
	}

	public Result<?> deleteUserData(DecodedJWT jwt) {
		USER_HANDLERS.forEach(userHandler -> userHandler.deleteUserData(UUID.fromString(jwt.getSubject())));

		return Result.empty();
	}

	private String getNameKey(UserDataHandler userHandler) {
		String nameKey = userHandler.getClass().getSimpleName();
		for (String prefixOrSuffix : PREFIXES_AND_SUFFIXES) nameKey = nameKey.replaceAll(prefixOrSuffix, "");

		return nameKey;
	}

}
