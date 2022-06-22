/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.auth;

import io.github.paexception.engelsburg.api.controller.reserved.SemesterController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
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
import static io.github.paexception.engelsburg.api.util.Constants.Semester.NAME_KEY;

/**
 * Handles declared semester parameters of requests.
 *
 * <p>This interceptor has to be executed after {@link AuthenticationInterceptor} because the user information is needed
 * to get the semesters.</p>
 */
@Component
@AllArgsConstructor
public class SemesterInterceptor extends HandlerInterceptorAdapter implements HandlerMethodArgumentResolver {

	private final SemesterController semesterController;

	/**
	 * Intercept to parse semester information.
	 *
	 * <p>If no semester is specified in the query use the current of the user</p>
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
		try {
			if (!(handler instanceof HandlerMethod)) return true;

			UserDTO user = (UserDTO) request.getAttribute("jwt");
			if (user == null) return true;

			SemesterModel semester;
			String param = request.getParameter("semester");
			if (param == null) {
				semester = user.user.getCurrentSemester();
			} else {
				Result<SemesterModel> res = this.semesterController.getSemesterRaw(Integer.parseInt(param), user);
				if (res.isErrorPresent() || res.isResultNotPresent()) {
					res.respond(response);
					return false;
				}
				semester = res.getResult();
			}

			if (semester != null) {
				request.setAttribute("semester", semester);
				return true;
			}
		} catch (Exception ignored) {
		}

		Result.of(Error.NOT_FOUND, NAME_KEY).respond(response);
		return false;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(SemesterModel.class);
	}

	@Override
	public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		return ((HttpServletRequest) webRequest.getNativeRequest()).getAttribute("semester");
	}
}
