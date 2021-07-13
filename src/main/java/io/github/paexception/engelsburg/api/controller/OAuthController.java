package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.oauth.OAuthHandler;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.OAuthModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.OAuthRepository;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginOAuthRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.LoginResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.OAuthSignupResponseDTO;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller to handle oauth requests.
 */
@Component
public class OAuthController implements UserDataHandler {

	@Autowired
	private OAuthRepository oAuthRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScopeController scopeController;
	@Autowired
	private RefreshTokenController refreshTokenController;

	/**
	 * Request a oauth login.
	 *
	 * @param schoolToken to verify
	 * @param service     to handle
	 * @param request     sent by user
	 * @param response    given by spring
	 * @return service specific result
	 */
	public Result<?> request(String schoolToken, String service, HttpServletRequest request, HttpServletResponse response) {
		if (!schoolToken.equals(Environment.SCHOOL_TOKEN)) //Check school token
			return Result.of(Error.FORBIDDEN, "school_token");

		for (OAuthHandler oAuthHandler : OAuthHandler.getOAuthHandlers()) {
			if (oAuthHandler.getName().equals(service))
				return oAuthHandler.resolveOAuthLoginRequest(request, response);
		}

		return Result.of(Error.NOT_FOUND, "oauth_service");
	}

	/**
	 * Handles redirects of oauth servers.
	 *
	 * @param service  to let the redirect handle
	 * @param request  to handle
	 * @param response given by spring
	 * @return signup of user
	 */
	public Result<?> redirect(String service, HttpServletRequest request, HttpServletResponse response) {
		for (OAuthHandler oAuthHandler : OAuthHandler.getOAuthHandlers()) {
			if (oAuthHandler.getName().equals(service))
				return this.signup(oAuthHandler.resolveOAuthResponse(request, response), oAuthHandler.getName());
		}

		return Result.of(Error.NOT_FOUND, "oauth_service");
	}

	/**
	 * Login a user via oauth.
	 *
	 * @param service to login with
	 * @param dto     with email and token to login
	 * @return refresh token
	 */
	public Result<LoginResponseDTO> login(String service, LoginOAuthRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		UserModel user = optionalUser.get();
		Optional<OAuthModel> optionalOAuth = this.oAuthRepository.findByUserIdAndService(user.getUserId(), service);
		if (optionalOAuth.isEmpty()) return Result.of(Error.NOT_FOUND, "oauth_token");

		OAuthModel oAuth = optionalOAuth.get();
		if (!oAuth.getToken().equals(AuthenticationController.hashPassword(dto.getToken(), oAuth.getSalt())))
			return Result.of(Error.FORBIDDEN, "token");

		return Result.of(new LoginResponseDTO(this.refreshTokenController.create(user.getUserId())));
	}

	/**
	 * Sign up a user via oauth.
	 * <p>
	 * Create new user if not exists.
	 * Register as verified.
	 * Password of user will be set random, if not already existed.
	 * </p>
	 *
	 * @param email   given by the oauth service implementation
	 * @param service the oauth service
	 * @return oauth service and token to login
	 */
	private Result<OAuthSignupResponseDTO> signup(String email, String service) {
		if (email == null || email.isBlank()) return Result.of(Error.FAILED, "oauth"); //Check errors

		Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);
		UserModel user;
		if (optionalUser.isEmpty()) { //Create new user
			UUID userId = UUID.randomUUID();

			AuthenticationController.DEFAULT_SCOPES.forEach(scope -> this.scopeController.addScope(userId, scope)); //Add default scopes
			AuthenticationController.VERIFIED_SCOPES.forEach(scope -> this.scopeController.addScope(userId, scope)); //Add verified scopes

			String salt = AuthenticationController.randomSalt(); //Assign random password
			String hashedPassword = AuthenticationController.hashPassword(null, salt);

			user = this.userRepository.save(new UserModel(-1, userId, email, hashedPassword, salt, true));
		} else user = optionalUser.get();

		Optional<OAuthModel> optionalOAuth = this.oAuthRepository.findByUserIdAndService(user.getUserId(), service);
		OAuthModel oAuth = optionalOAuth.orElse(new OAuthModel());
		if (optionalOAuth.isEmpty()) { //Check if already signed up with this oauth service
			oAuth.setUserId(user.getUserId());
			oAuth.setOauthId(-1);
			oAuth.setService(service);
		}

		String token = RandomStringUtils.randomAlphanumeric(50);
		String salt = AuthenticationController.randomSalt();
		oAuth.setToken(AuthenticationController.hashPassword(token, salt));
		oAuth.setSalt(salt);

		this.oAuthRepository.save(oAuth);

		return Result.of(new OAuthSignupResponseDTO(service, token));
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.oAuthRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.oAuthRepository.findAllByUserId(userId));
	}

}
