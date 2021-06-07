package io.github.paexception.engelsburg.api.spring;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburg.api.EngelsburgAPI;
import io.github.paexception.engelsburg.api.spring.interceptor.ScopeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Class to configure Interceptors, Mappings, etc.
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer, HandlerMethodArgumentResolver {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET")
				.allowCredentials(false).maxAge(3600);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.stream()
				.filter(x -> x instanceof MappingJackson2HttpMessageConverter)
				.forEach(x -> ((MappingJackson2HttpMessageConverter) x).setObjectMapper(this.mapper));
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return ((HttpServletRequest) webRequest.getNativeRequest()).getAttribute("jwt");
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(DecodedJWT.class);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(this);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<HandlerInterceptorAdapter> handlerInterceptors = List.of(
				new ScopeInterceptor(EngelsburgAPI.getJwtUtil())
		);

		for (HandlerInterceptorAdapter interceptors : handlerInterceptors)
			registry.addInterceptor(interceptors).addPathPatterns("/**/*");
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

}
