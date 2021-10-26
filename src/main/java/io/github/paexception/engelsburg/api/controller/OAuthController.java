package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.oauth.OAuthHandler;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Pair;
import io.github.paexception.engelsburg.api.util.Result;
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
public class OAuthController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScopeController scopeController;
	@Autowired
	private AuthenticationController authenticationController;

	/**
	 * Request a oauth login.
	 *
	 * @param schoolToken to verify
	 * @param service     to handle
	 * @param request     sent by user
	 * @param response    given by spring
	 * @return service specific result
	 */
	public Result<?> request(String schoolToken, String service, HttpServletRequest request,
			HttpServletResponse response) {
		boolean signUp = !schoolToken.isEmpty();
		if (!signUp && !Environment.SCHOOL_TOKEN.equals(schoolToken)) { //Check school token
			return Result.of(Error.FORBIDDEN, "school_token");
		}

		for (OAuthHandler oAuthHandler : OAuthHandler.getOAuthHandlers()) {
			if (oAuthHandler.getName().equals(service))
				return oAuthHandler.resolveOAuthLoginRequest(request, response, signUp);
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
				return this.signup(oAuthHandler.resolveOAuthResponse(request, response));
		}

		return Result.of(Error.NOT_FOUND, "oauth_service");
	}

	/**
	 * Sign up or login a user via oauth.
	 * <p>
	 * Create new user if not exists.
	 * Register as verified.
	 * Password of user will be set random, if not already existed.
	 * </p>
	 *
	 * @param emailAndSignUp given by the oauth service implementation
	 * @return refresh token
	 */
	private Result<AuthResponseDTO> signup(Pair<String, Boolean> emailAndSignUp) {
		if (emailAndSignUp.getLeft() == null || emailAndSignUp.getLeft().isBlank())
			return Result.of(Error.FAILED, "oauth"); //Check errors

		Optional<UserModel> optionalUser = this.userRepository.findByEmail(emailAndSignUp.getLeft());
		UserModel user;
		if (optionalUser.isEmpty() && !emailAndSignUp.getRight()) {
			return Result.of(Error.NOT_FOUND, "user");
		} else if (optionalUser.isEmpty()) { //Create new user
			UUID userId = UUID.randomUUID();

			AuthenticationController.DEFAULT_SCOPES.forEach(
					scope -> this.scopeController.addScope(userId, scope)); //Add default scopes
			AuthenticationController.VERIFIED_SCOPES.forEach(
					scope -> this.scopeController.addScope(userId, scope)); //Add verified scopes

			String salt = AuthenticationController.randomSalt(); //Assign random password
			String hashedPassword = AuthenticationController.hashPassword(null, salt);

			user = this.userRepository.save(
					new UserModel(-1, userId, emailAndSignUp.getLeft(), hashedPassword, salt, true));
		} else {
			user = optionalUser.get();
			if (!user.isVerified()) {
				AuthenticationController.VERIFIED_SCOPES.forEach(
						scope -> this.scopeController.addScope(user.getUserId(), scope)); //Add verified scopes
				user.setVerified(true);
				this.userRepository.save(user);
			}
		}

		return Result.of(this.authenticationController.createAuthResponse(user));
	}

}
