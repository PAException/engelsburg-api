/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.JWTCreator;
import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.controller.internal.UserController;
import io.github.paexception.engelsburg.api.controller.internal.UserOAuthController;
import io.github.paexception.engelsburg.api.controller.internal.UserPasswordController;
import io.github.paexception.engelsburg.api.controller.reserved.SemesterController;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserPasswordModel;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.service.email.EmailService;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Controller for authentication.
 */
@Component
@AllArgsConstructor
public class AuthenticationController {

	private final UserController userController;
	private final UserPasswordController userPasswordController;
	private final UserOAuthController userOAuthController;

	private final ScopeController scopeController;
	private final TokenController tokenController;
	private final RefreshTokenController refreshTokenController;

	private final EmailService emailService;

	/**
	 * Sign up with credentials and schoolToken.
	 *
	 * @param dto email, password and schoolToken
	 * @return empty response or error
	 */
	@Transactional
	public Result<AuthResponseDTO> signUp(SignUpRequestDTO dto) {
		//If user by email exists return error
		if (this.userPasswordController.existsByEmail(dto.getEmail())) return Result.of(Error.ALREADY_EXISTS, "user");

		//Create new user and userPassword
		UserModel user = this.userController.create(dto.getEmail(), false);
		this.userPasswordController.create(user, dto.getEmail(), dto.getPassword());

		//Add default scopes to user
		this.scopeController.addDefaultScopes(user);

		//Verify email with created token
		if (this.emailService.verify(dto.getEmail(), this.tokenController
				.createRandomToken(user, "verify", user.getUserId().toString())))
			//Return authentication response
			return Result.of(this.createAuthResponse(user));
		else {
			//If something goes wrong while sending the email return an unexpected error
			this.userController.delete(user);
			return Result.of(Error.FAILED, "signup");
		}
	}

	/**
	 * Login with credentials.
	 *
	 * @param dto email and password
	 * @return valid jwt token
	 */
	public Result<AuthResponseDTO> login(LoginRequestDTO dto) {
		//Get userPassword if not present return error
		Optional<UserPasswordModel> optionalUserPassword = this.userPasswordController.getByEmail(dto.getEmail());
		if (optionalUserPassword.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		//Check if password is correct then return authentication response otherwise return error
		UserPasswordModel userPassword = optionalUserPassword.get();
		if (UserPasswordController.verifyPasswort(userPassword, dto.getPassword()))
			return Result.of(this.createAuthResponse(userPassword.getUser()));
		else return Result.of(Error.FORBIDDEN, "wrong_password");
	}

	/**
	 * Authenticate and request JWT via refresh token.
	 *
	 * @param refreshToken to get JWT
	 * @return JWT
	 */
	public Result<AuthResponseDTO> auth(String refreshToken) {
		//Get user by verifying refreshToken, if verifying fails return error
		UserModel user = this.refreshTokenController.verifyRefreshToken(refreshToken);
		if (user == null) return Result.of(Error.FAILED, "refreshToken");

		//Return authentication response
		return Result.of(this.createAuthResponse(user));
	}

	/**
	 * Request to reset a password of existing account.
	 * An email will be sent with a token to reset the password.
	 * A user can also set his password with this method if the account was created via an OAuth method.
	 *
	 * @param email   of account to reset
	 * @param userDTO to create password authentication if account was created via OAuth
	 * @return empty response
	 */
	public Result<?> requestResetPassword(String email, UserDTO userDTO) {
		//Get userPassword by email if not present and no user is authenticated return error
		Optional<UserPasswordModel> optionalUserPassword = this.userPasswordController.getByEmail(email);
		if (optionalUserPassword.isEmpty() && userDTO != null)
			optionalUserPassword = this.userPasswordController.get(userDTO.user);
		if (optionalUserPassword.isEmpty() && userDTO == null) return Result.of(Error.NOT_FOUND, "user");

		//Set user and email
		UserModel user = optionalUserPassword.map(UserPasswordModel::getUser).orElseGet(() -> userDTO.user);
		if (userDTO != null && optionalUserPassword.isPresent()) email = optionalUserPassword.get().getEmail();

		//Create a new token and email to reset the password. If something goes wrong by sending the email return error
		long exp = System.currentTimeMillis() + 1000 * 60 * 30;
		String token = this.tokenController
				.createRandomTemporaryToken(user, "reset_password", exp, email, user.getUserId().toString());
		if (!this.emailService.resetPassword(email, token)) return Result.of(Error.FAILED, "request_reset_password");

		//Return empty result
		return Result.empty();
	}

	/**
	 * Set the new password with password reset token or if the user is authenticated.
	 * Reset by authenticated user is only possible if no password has been specified before.
	 * Use case is intended to set a password if a user signed up via OAuth method.
	 *
	 * @param dto with email, new password and password reset token
	 * @return empty result or error
	 */
	public Result<?> resetPassword(ResetPasswordRequestDTO dto) {
		//Check if token is valid, otherwise return error
		if (!this.tokenController.checkToken("reset_password", dto.getToken()))
			return Result.of(Error.FAILED, "reset_password");

		//Get params
		String[] params = this.tokenController.getParams("reset_password", dto.getToken());
		if (params.length < 2)
			throw new IllegalStateException("Suffix must be present, was created in request password reset");

		//Check if user email already has a password
		Optional<UserPasswordModel> optionalUserPassword = this.userPasswordController.getByEmail(params[0]);
		if (optionalUserPassword.isEmpty()) {
			//Get user by param
			UserModel user = this.userController.get(UUID.fromString(params[1]));
			if (user == null) throw new IllegalStateException("User must be present, was set in request password");

			//Create a new password
			this.userPasswordController.create(user, params[0], dto.getPassword());
			this.tokenController.deleteToken(user, "reset_password", dto.getToken());
		} else {
			//Update the password
			UserPasswordModel userPassword = optionalUserPassword.get();
			this.userPasswordController.updatePassword(userPassword, dto.getPassword());
			this.tokenController.deleteToken(userPassword.getUser(), "reset_password", dto.getToken());

			//If the user reset the password then remove all refresh tokens to force a re-login
			this.refreshTokenController.deleteRefreshTokensOfUser(userPassword.getUser());
		}

		//Return empty result
		return Result.empty();
	}

	/**
	 * Verifies a user.
	 *
	 * @param token to verify
	 * @return empty response or error
	 */
	public Result<AuthResponseDTO> verify(String token) {
		//Check if token is valid, otherwise return error
		if (!this.tokenController.checkToken("verify", token)) return Result.of(Error.FAILED, "verify");

		//Get params
		String[] params = this.tokenController.getParams("verify", token);
		if (params.length < 1)
			throw new IllegalStateException("Params must be present, was created in request password reset");

		//Get user by param
		UserModel user = this.userController.get(UUID.fromString(params[0]));
		if (user == null) throw new IllegalStateException("User must be present, was set in request password");

		//Verify user and add verified scopes
		this.userController.verify(user);
		this.scopeController.addVerifiedScopes(user);

		//Delete token and return empty result
		this.tokenController.deleteToken(user, "verify", token);
		return Result.of(this.createAuthResponse(user));
	}

	/**
	 * Request a specific scopes to grant to the user.
	 *
	 * @param requestParams request params with scope and verification
	 * @param userDTO       user to grant scope to
	 * @return auth with updated scopes
	 */
	public Result<AuthResponseDTO> requestScopes(Map<String, String> requestParams, UserDTO userDTO) {
		//Search through all validators if they validate that scope, then validate if
		List<String> scopes = new ArrayList<>();
		List<String> failed = new ArrayList<>();
		for (ScopeRequestValidator validator : ScopeRequestValidator.VALIDATORS) {
			if (!requestParams.containsKey(validator.scope)) continue;
			if (validator.validate.apply(requestParams.get(validator.scope))) scopes.add(validator.scope);
			else failed.add(validator.scope);
		}

		//If no scopes found return error, else grant scope
		if (scopes.isEmpty()) return Result.of(Error.FAILED, StringUtils.joinWith(",", failed.toArray()));
		for (String grant : scopes) this.scopeController.addScope(userDTO.user, grant);

		//Return auth response
		return Result.of(this.createAuthResponse(userDTO.user));
	}

	/**
	 * Update default scopes of all current users.
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void updateDefaultScopes() {
		//Get all users, update scopes
		List<UserModel> all = this.userController.getAll();
		List<UserModel> verified = this.userController.getAllVerified();
		this.scopeController.updateDefaultScopes(all, verified);
	}

	/**
	 * Create a valid jwt token.
	 *
	 * @param user uuid as subject of jwt and verified information
	 * @return jwt token
	 */
	public AuthResponseDTO createAuthResponse(UserModel user) {
		String[] scopes = this.scopeController.getScopes(user);
		//Create jwtBuilder with userId as subject and 5 minute expiration
		JWTCreator.Builder jwtBuilder = JwtUtil.getInstance()
				.createBuilder(user.getUserId().toString(), 5, Calendar.MINUTE)
				.withClaim("scopes", ScopeController.mergeScopes(scopes));
		String jwt = JwtUtil.getInstance().sign(jwtBuilder);

		String refreshToken = this.refreshTokenController.create(user);

		String email = this.userPasswordController.get(user).map(UserPasswordModel::getEmail).orElse(null);
		String[] loginVia = this.userOAuthController.getServicesByUser(user);
		if (email != null) {
			loginVia = Arrays.copyOf(loginVia, loginVia.length + 1);
			loginVia[loginVia.length - 1] = "email";
		}

		String className = null;
		if (user.getCurrentSemester() != null) {
			className = SemesterController.classNameBySemester(user.getCurrentSemester().getSemester());
			className += user.getCurrentSemester().getClassSuffix();
		}


		//Create JWT with additional merged scopes and return with refreshToken, accountName and if the user is verified
		return new AuthResponseDTO(
				jwt,
				refreshToken,
				email,
				user.getUsername(),
				className,
				loginVia,
				user.isVerified()
		);
	}

	/**
	 * Used to verify scopes.
	 */
	@Getter
	@AllArgsConstructor
	static class ScopeRequestValidator {
		public static final List<ScopeRequestValidator> VALIDATORS = List.of(
				new ScopeRequestValidator("substitute.read.current", Environment.SCHOOL_TOKEN::equals),
				new ScopeRequestValidator("substitute.message.read.current", Environment.SCHOOL_TOKEN::equals),
				new ScopeRequestValidator("info.teacher.read.all", Environment.SCHOOL_TOKEN::equals),
				new ScopeRequestValidator("info.classes.read.all", Environment.SCHOOL_TOKEN::equals)
		);

		private final String scope;
		private final Function<String, Boolean> validate;
	}
}
