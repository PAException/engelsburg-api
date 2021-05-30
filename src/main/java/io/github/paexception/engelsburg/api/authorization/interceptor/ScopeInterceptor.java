package io.github.paexception.engelsburg.api.authorization.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburg.api.authorization.JwtUtil;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Pair;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ScopeInterceptor extends HandlerInterceptorAdapter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

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
		if (jwt == null || jwt.isBlank()) return false;

		Pair<DecodedJWT, JwtUtil.VerificationResult> result = this.jwtUtil.verify(jwt);
		switch (result.getRight()) {
			case EXPIRED:
				this.respond(response, Result.of(Error.EXPIRED, "token").getHttpResponse());
				return false;
			case INVALID:
				this.respond(response, Result.of(Error.INVALID, "token").getHttpResponse());
				return false;
			case SUCCESS:
				break;
			default://FAILED, UNKNOWN
				this.respond(response, Result.of(Error.FAILED, "token").getHttpResponse());
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

	private void respond(HttpServletResponse response, ResponseEntity<Object> responseEntity) throws IOException {
		response.setStatus(responseEntity.getStatusCodeValue());
		for (Map.Entry<String, List<String>> header : responseEntity.getHeaders().entrySet())
			for (String valor : header.getValue()) response.addHeader(header.getKey(), valor);
		response.getWriter().write(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));
		response.getWriter().flush();
	}

}
