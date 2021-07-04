package io.github.paexception.engelsburg.api.spring.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PagingInterceptor extends HandlerInterceptorAdapter implements HandlerMethodArgumentResolver {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String paramPage = request.getParameter("page"),
				paramSize = request.getParameter("size");
		Paging paging = new Paging();

		try {
			paging.setPage(Integer.parseInt(paramPage));
			try {
				paging.setSize(Integer.parseInt(paramSize));
			} catch (NumberFormatException e) {
				paging.setSize(Integer.MAX_VALUE);
			}

		} catch (NumberFormatException ignored) {
		}
		request.setAttribute("paging", paging);

		return true;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(Paging.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return ((HttpServletRequest) webRequest.getNativeRequest()).getAttribute("paging");
	}

}
