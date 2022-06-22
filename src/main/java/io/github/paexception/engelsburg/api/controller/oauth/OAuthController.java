/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.oauth;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.UserController;
import io.github.paexception.engelsburg.api.controller.internal.UserOAuthController;
import io.github.paexception.engelsburg.api.controller.internal.UserPasswordController;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserOAuthModel;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Controller to handle OAuth requests.
 */
@Component
@AllArgsConstructor
public class OAuthController {

	private final ScopeController scopeController;
	private final AuthenticationController authenticationController;

	private final UserController userController;
	private final UserOAuthController userOAuthController;
	private final UserPasswordController userPasswordController;

	/**
	 * Request an OAuth signup via pre requested access token to actual OAuth API of service.
	 * This method only validates the accessToken and creates a new account.
	 *
	 * <p>
	 * Create new user
	 * Register as verified.
	 * </p>
	 *
	 * @param accessToken AccessToken to verify the email
	 * @param service     Service where the oauth request was sent to and the accessToken is from
	 * @return Error or login data
	 */
	public Result<AuthResponseDTO> signUp(String service, String accessToken) {
		//Get result of service specific Handler
		Result<OAuthHandler.HandlerResult> result = this.service(service, accessToken);
		if (result.isErrorPresent()) return Result.ret(result);
		OAuthHandler.HandlerResult handlerResult = result.getResult();

		//Return error if oauth login method already exists
		if (this.userOAuthController.exists(service, handlerResult.getIdentification()))
			return Result.of(Error.ALREADY_EXISTS, "user");

		//Create user and oauth method
		UserModel user = this.userController.create(handlerResult.getUsername(), true);
		this.userOAuthController.create(user, service, handlerResult.getIdentification(), handlerResult.getUsername());

		//Grant scopes to user
		this.scopeController.addDefaultScopes(user);
		this.scopeController.addVerifiedScopes(user);
		for (String scope : handlerResult.getGrantScopes()) this.scopeController.addScope(user, scope);

		//Return auth response
		return Result.of(this.authenticationController.createAuthResponse(user));
	}

	/**
	 * Login via OAuthMethod.
	 *
	 * @param service     to login
	 * @param accessToken to verify login
	 * @return Error or login data
	 */
	public Result<AuthResponseDTO> login(String service, String accessToken) {
		//Get result of service specific Handler
		Result<OAuthHandler.HandlerResult> result = this.service(service, accessToken);
		if (result.isErrorPresent()) return Result.ret(result);
		OAuthHandler.HandlerResult handlerResult = result.getResult();

		//Return error of oauth method was not found
		Optional<UserOAuthModel> optionalUserOAuth = this.userOAuthController
				.get(service, handlerResult.getIdentification());
		if (optionalUserOAuth.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		return Result.of(this.authenticationController.createAuthResponse(optionalUserOAuth.get().getUser()));
	}

	/**
	 * Connect an OAuthMethod to an existing user.
	 *
	 * @param service     to connect
	 * @param accessToken to verify connection
	 * @param userDTO     to connect to
	 * @return Error or login data
	 */
	public Result<AuthResponseDTO> connectToAccount(String service, String accessToken, UserDTO userDTO) {
		//Get result of service specific Handler
		Result<OAuthHandler.HandlerResult> result = this.service(service, accessToken);
		if (result.isErrorPresent()) return Result.ret(result);
		OAuthHandler.HandlerResult handlerResult = result.getResult();

		//Check if oauth login method is already existing
		if (this.userOAuthController.exists(service, handlerResult.getIdentification()))
			return Result.of(Error.ALREADY_EXISTS, "oauth");

		//Create new oauth login method
		UserModel user = userDTO.user;
		this.userOAuthController.create(user, service, handlerResult.getIdentification(), handlerResult.getUsername());

		//Add verified scopes
		if (!user.isVerified()) {
			this.scopeController.addVerifiedScopes(user);
			this.userController.verify(user);
		}
		//Add scopes validated by oauth service
		for (String scope : handlerResult.getGrantScopes()) this.scopeController.addScope(user, scope);

		return Result.of(this.authenticationController.createAuthResponse(user));
	}

	/**
	 * Disconnect an OAuthMethod from user.
	 *
	 * @param service to disconnect
	 * @param userDTO logged in user to delete by
	 * @return Error or empty Result
	 */
	public Result<?> disconnectFromAccount(String service, UserDTO userDTO) {
		//Check if service is supported
		if (!OAuthHandler.supports(service)) return Result.of(Error.NOT_FOUND, "oauth_service");

		//Cannot disconnect if only one method to login exists
		if (this.userOAuthController.getServicesByUser(userDTO.user).length <= 1
				&& this.userPasswordController.get(userDTO.user).isEmpty())
			return Result.of(Error.FAILED, "oauth");

		//Check if oauth method exists
		Optional<UserOAuthModel> optionalUserOAuth = this.userOAuthController
				.getByUserAndService(userDTO.user, service);
		if (optionalUserOAuth.isEmpty()) return Result.of(Error.NOT_FOUND, "oauth");

		//Delete oauth method
		this.userOAuthController.delete(optionalUserOAuth.get());

		return Result.empty();
	}

	/**
	 * Get result of OAuthHandler of specific service.
	 *
	 * @param service     to specify OAuthHandler
	 * @param accessToken to verify user
	 * @return HandlerResult or error
	 */
	private Result<OAuthHandler.HandlerResult> service(String service, String accessToken) {
		//Check if service is supported
		if (!OAuthHandler.supports(service))
			return Result.of(Error.NOT_FOUND, "oauth_service");

		//Get result of handler
		OAuthHandler.HandlerResult handlerResult = null;
		for (OAuthHandler oAuthHandler : OAuthHandler.getHandlers())
			if (oAuthHandler.getName().equals(service))
				handlerResult = oAuthHandler.verifyOAuthLoginRequest(accessToken);

		//Check if error occurred in controller
		if (handlerResult == null || handlerResult.getUsername().isBlank())
			return Result.of(Error.FAILED, "oauth");
		else return Result.of(handlerResult);
	}
}
