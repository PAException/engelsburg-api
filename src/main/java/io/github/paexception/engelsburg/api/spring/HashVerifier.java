/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.List;

/**
 * ControllerAdvice to compare sent hash and hash of response to check if content has changed.
 * Return NOT_MODIFIED if content hasn't changed.
 */
@ControllerAdvice
@NoArgsConstructor
public class HashVerifier implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(@NonNull MethodParameter returnType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
			@NonNull MediaType selectedContentType,
			@NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		List<String> oldHash = request.getHeaders().get("Hash");
		List<String> newHash = response.getHeaders().get("Hash");

		if (oldHash != null && newHash != null)
			if (oldHash.size() != 0 && newHash.size() != 0)
				if (oldHash.get(0).trim().equals(newHash.get(0).trim())) {
					response.setStatusCode(HttpStatus.NOT_MODIFIED);
					return null;
				}

		return body;
	}

}
