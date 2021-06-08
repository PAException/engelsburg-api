package io.github.paexception.engelsburg.api.spring.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.spring.AuthScope;
import io.github.paexception.engelsburg.api.spring.Authorization;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import io.github.paexception.engelsburg.api.util.Pair;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class ScopeInterceptor extends HandlerInterceptorAdapter {

	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
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

		Pair<DecodedJWT, JwtUtil.VerificationResult> result = this.jwtUtil.verify(jwt);
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

		Claim claim = result.getLeft().getClaim("scopes");
		List<String> userScopes = claim.asList(String.class);
		for (AuthScope authScope : authScopes) {
			String scope = authScope.scope();
			if (scope.isBlank()) scope = authScope.value();
			if (!scope.isBlank()) {
				boolean isPresent = false;
				for (String userScope : userScopes)
					if (userScope.equalsIgnoreCase(scope)) {
						isPresent = true;
						break;
					}
				if (!isPresent) return false;
			}
		}

		request.setAttribute("jwt", result.getLeft());

		return true;
	}

}
