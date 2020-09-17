package io.github.paexception.engelsburginfrastructure.spring;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
public class ServiceTokenInterceptor extends HandlerInterceptorAdapter {

	private final String serviceToken;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		String serviceToken = request.getHeader("ServiceToken");

		if (serviceToken == null) return false;
		if (!this.serviceToken.equals(serviceToken)) return false;

		response.setStatus(HttpStatus.OK.value());
		return true;
	}

}
