package io.github.paexception.engelsburg.api.controller.oauth;

import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.RandomStringUtils;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class helps to handle oauth verifications.
 */
public abstract class OAuthHandler {

	private static final Set<OAuthHandler> O_AUTH_HANDLERS = new HashSet<>();
	private final Set<String> tokens = new HashSet<>();

	/**
	 * Default function to redirect.
	 *
	 * @param response to redirect
	 * @param redirect to redirect to
	 * @return empty result or error
	 */
	protected static Result<?> defaultRedirect(HttpServletResponse response, String redirect) {
		try {
			response.sendRedirect(redirect);
		} catch (IOException e) {
			Result.of(Error.FAILED, "oauth");
		}

		return Result.empty();
	}

	/**
	 * Creates a random alphanumeric to secure that the server sends the redirect.
	 * Should be contained in the redirect or rather in the oauth request.
	 *
	 * @return random alphanumeric token
	 */
	protected final String createToken() {
		String token = RandomStringUtils.randomAlphanumeric(20);
		this.tokens.add(token);

		return token;
	}

	/**
	 * Verifies alphanumeric token sent in request.
	 *
	 * @param token to verify
	 * @return true if token existed
	 */
	protected final boolean verifyAndDeleteToken(String token) {
		return this.tokens.remove(token);
	}

	/**
	 * Register all oauthControllers.
	 */
	@PostConstruct
	public final void registerOAuthController() {
		O_AUTH_HANDLERS.add(this);
	}

	/**
	 * Handles the OAuthResponse of the server.
	 *
	 * @param request  sent by the server
	 * @param response given by spring
	 * @return email
	 */
	abstract public String resolveOAuthResponse(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Handle request to login with oauth.
	 *
	 * @param request  of user
	 * @param response given by spring
	 * @return result
	 */
	abstract public Result<?> resolveOAuthLoginRequest(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Getter of all oauth handlers.
	 *
	 * @return all oauth handlers.
	 */
	public static OAuthHandler[] getOAuthHandlers() {
		return O_AUTH_HANDLERS.toArray(OAuthHandler[]::new);
	}

	/**
	 * Get the service specific redirect uri to process the oauth response.
	 *
	 * @return redirect uri
	 */
	protected final String getRedirectUri() {
		return Environment.OAUTH2_REDIRECT_URI + "/" + this.getName();
	}

	/**
	 * Get the name of the {@link OAuthHandler} to identify endpoints.
	 *
	 * @return the name
	 */
	abstract public String getName();

}
