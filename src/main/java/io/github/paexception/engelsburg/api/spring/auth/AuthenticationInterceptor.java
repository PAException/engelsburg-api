package io.github.paexception.engelsburg.api.spring.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import io.github.paexception.engelsburg.api.util.Pair;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Handles authentication via {@link AuthScope} and {@link Authorization}.
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter implements HandlerMethodArgumentResolver {

	private final AuthenticationController authenticationController;

	public AuthenticationInterceptor(
			AuthenticationController authenticationController) {
		this.authenticationController = authenticationController;
	}

	/**
	 * Preprocessing all authorization of user.
	 *
	 * <p>Check for needed authorization of user and authorize if</p>
	 * <p>Verify JWT token</p>
	 * <p>Check if any scopes are needed to proceed</p>
	 * <p>Create UserDTO to pass to requested endpoint as argument with user info</p>
	 *
	 * @param request  sent
	 * @param response to write possible errors to
	 * @param handler  to get method annotations
	 * @return if request should continue
	 * @throws IOException if e.g. response writing fails
	 */
	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull
			Object handler) throws IOException {
		HandlerMethod method = (HandlerMethod) handler;

		AuthScope[] authScopes;
		Authorization auth = method.getMethodAnnotation(Authorization.class);
		AuthScope methodAuthScope = method.getMethodAnnotation(AuthScope.class);
		if (auth != null) authScopes = auth.value();
		else {
			if (methodAuthScope == null) return true;
			authScopes = new AuthScope[]{methodAuthScope};
		}

		String jwt = request.getHeader("Authorization");
		if (jwt == null || jwt.isBlank()) {
			Result.of(Error.UNAUTHORIZED, "token").respond(response);
			return false;
		}

		Pair<DecodedJWT, JwtUtil.VerificationResult> result = JwtUtil.getInstance().verify(jwt);
		switch (result.getRight()) {
			case EXPIRED:
				Result.of(Error.EXPIRED, "token").respond(response);
				return false;
			case INVALID:
				Result.of(Error.INVALID, "token").respond(response);
				return false;
			case SUCCESS:
				break;
			default://FAILED, UNKNOWN
				Result.of(Error.FAILED, "token").respond(response);
				return false;
		}

		UserDTO userDTO = new UserDTO(
				result.getLeft(),
				this.authenticationController.getUser(UUID.fromString(result.getLeft().getSubject()))
		);

		for (AuthScope authScope : authScopes) {
			String scope = authScope.scope();
			if (scope.isBlank()) scope = authScope.value();
			if (!scope.isBlank()) {
				if (!userDTO.hasScope(scope)) {
					Result.of(Error.FORBIDDEN, "auth").respond(response);
					return false;
				}
			}
		}

		request.setAttribute("jwt", userDTO);

		return true;
	}

	@Override
	public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		return ((HttpServletRequest) webRequest.getNativeRequest()).getAttribute("jwt");
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(UserDTO.class);
	}

}
