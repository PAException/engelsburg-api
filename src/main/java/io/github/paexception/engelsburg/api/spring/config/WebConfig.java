/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburg.api.spring.paging.PagingInterceptor;
import io.github.paexception.engelsburg.api.spring.rate_limiting.RateLimitInterceptor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * Class to configure Interceptors, Mappings, etc.
 */
@Configuration
@EnableWebMvc
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final PagingInterceptor pagingInterceptor;
	private final RateLimitInterceptor rateLimitInterceptor;
	private final ObjectMapper mapper;

	/**
	 * Configure mapping, origins, methods, etc.
	 *
	 * @param registry to configure
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "DELETE", "PATCH", "PUT")
				.allowCredentials(false).maxAge(3600);
	}

	/**
	 * Set custom object mapper.
	 *
	 * @param converters to set mapper of converter
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.stream()
				.filter(x -> x instanceof MappingJackson2HttpMessageConverter)
				.forEach(x -> ((MappingJackson2HttpMessageConverter) x).setObjectMapper(this.mapper));
	}

	/**
	 * Add ArgumentResolvers for custom endpoint method arguments.
	 *
	 * @param resolvers to add to
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.addAll(List.of(this.pagingInterceptor));
	}

	/**
	 * Add Interceptors to intercept requests.
	 * Mostly used for verification and to resolve arguments.
	 *
	 * @param registry to add interceptors to
	 */
	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		registry.addInterceptor(this.rateLimitInterceptor)
				.order(Ordered.HIGHEST_PRECEDENCE)
				.addPathPatterns("/**/*");
		registry.addInterceptor(this.pagingInterceptor)
				.order(Ordered.LOWEST_PRECEDENCE)
				.addPathPatterns("/**/*");
	}

	/**
	 * Configure default content type (json).
	 *
	 * @param configurer to set content type
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * Endpoint docs config.
	 *
	 * @param registry given
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
