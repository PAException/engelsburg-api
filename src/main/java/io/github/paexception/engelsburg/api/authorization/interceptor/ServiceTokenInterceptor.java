package io.github.paexception.engelsburg.api.authorization.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor to check if a function or a class needs a ServiceToken check.
 * If that is applicable check if the ServiceToken is correct
 */
@AllArgsConstructor
public class ServiceTokenInterceptor extends HandlerInterceptorAdapter {

	private final String serviceToken;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		response.setStatus(HttpStatus.FORBIDDEN.value());

		HandlerMethod method = (HandlerMethod) handler;
		if (method.hasMethodAnnotation(IgnoreServiceToken.class) ||
				method.getMethod().getDeclaringClass().getAnnotation(IgnoreServiceToken.class) != null) {
			return true;
		}

		String serviceToken = request.getHeader("X-ServiceToken");

		if (serviceToken == null) return false;
		return this.serviceToken.equals(serviceToken);
	}

}
