package io.github.paexception.engelsburg.api.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburg.api.EngelsburgAPI;
import io.github.paexception.engelsburg.api.spring.auth.ScopeInterceptor;
import io.github.paexception.engelsburg.api.spring.paging.PagingInterceptor;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * Class to configure Interceptors, Mappings, etc.
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	private final ScopeInterceptor scopeInterceptor = new ScopeInterceptor(EngelsburgAPI.getJWT_UTIL());
	private final PagingInterceptor pagingInterceptor = new PagingInterceptor();
	private final RateLimitInterceptor rateLimiter = new RateLimitInterceptor();
	@Autowired
	private ObjectMapper mapper;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "DELETE", "PATCH", "PUT")
				.allowCredentials(false).maxAge(3600);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.stream()
				.filter(x -> x instanceof MappingJackson2HttpMessageConverter)
				.forEach(x -> ((MappingJackson2HttpMessageConverter) x).setObjectMapper(this.mapper));
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.addAll(List.of(this.pagingInterceptor, this.scopeInterceptor));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<HandlerInterceptor> handlerInterceptors = List.of(this.pagingInterceptor, this.scopeInterceptor,
				this.rateLimiter);

		for (HandlerInterceptor interceptors : handlerInterceptors)
			registry.addInterceptor(interceptors).addPathPatterns("/**/*");
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
	}

}
