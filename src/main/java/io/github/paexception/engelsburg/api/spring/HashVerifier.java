package io.github.paexception.engelsburg.api.spring;

import lombok.NoArgsConstructor;
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
 * Interceptor to compare send hash and hash of response to check if content has changed
 */
@ControllerAdvice
@NoArgsConstructor
public class HashVerifier implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		List<String> oldHash = request.getHeaders().get("Hash");
		List<String> newHash = response.getHeaders().get("Hash");

		if (oldHash != null && newHash != null)
			if (oldHash.size() != 0 && newHash.size() != 0)
				if (oldHash.get(0).trim().equals(newHash.get(0).trim()))
					response.setStatusCode(HttpStatus.NOT_MODIFIED);

		return body;
	}

}