package io.github.paexception.engelsburg.api.spring.interceptor;

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
		HandlerMethod method = (HandlerMethod) handler;
		if (!method.hasMethodAnnotation(ServiceToken.class) ||
				method.getMethod().getDeclaringClass().getAnnotation(ServiceToken.class) == null)
			return true;

		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		String serviceToken = request.getHeader("ServiceToken");

		if (serviceToken == null) return false;
		if (!this.serviceToken.equals(serviceToken)) return false;

		response.setStatus(HttpStatus.OK.value());
		return true;
	}

}
