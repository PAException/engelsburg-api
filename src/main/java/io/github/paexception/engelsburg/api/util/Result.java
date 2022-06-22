/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Class to return on endpoints.
 * It can handle any type as well as errors and format them properly as a http response
 *
 * @param <T> any type to respond
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private T result;
	private Error error;
	private String extra;

	public static <T> Result<T> empty() {
		return new Result<>();
	}

	public static <T> Result<T> of(T result) {
		return of(result, null);
	}

	public static <T> Result<T> of(T result, String extra) {
		Result<T> instance = new Result<>();
		instance.setResult(result);
		if (extra != null) instance.setExtra(extra);
		return instance;
	}

	public static <T> Result<T> of(Error error) {
		return of(error, null);
	}

	public static <T> Result<T> of(Error error, String extra) {
		Result<T> instance = new Result<>();
		instance.setError(error);
		if (extra != null) instance.setExtra(extra);
		return instance;
	}

	/**
	 * Maps the given instance to an instance with other generics.
	 *
	 * @param <T>      The new generics
	 * @param instance The instance to map
	 * @return the given instance
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> Result<T> ret(Result instance) {
		return instance;
	}

	/**
	 * Hash function for header on response.
	 *
	 * @param o The object o to hash
	 * @return hash of o
	 */
	public static String hash(Object o) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : Hash.sha1(o)) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	/**
	 * Map a result to a new result.
	 * If error present nothing gets mapped.
	 *
	 * @param map Apply to result
	 * @param <S> new Type
	 * @return new Result
	 */
	public <S> Result<S> map(Function<T, S> map) {
		if (this.isErrorPresent()) return ret(this);

		return Result.of(map.apply(this.result));
	}

	/**
	 * Convert Result in a HttpServletResponse for preHandles.
	 *
	 * @param response HttpServletResponse given by preHandle method
	 * @throws IOException if there's an error writing the response
	 */
	public void respond(HttpServletResponse response) throws IOException {
		ResponseEntity<Object> responseEntity = this.getHttpResponse();
		response.setStatus(responseEntity.getStatusCodeValue());
		for (Map.Entry<String, List<String>> header : responseEntity.getHeaders().entrySet())
			for (String valor : header.getValue()) response.addHeader(header.getKey(), valor);
		response.getWriter().write(
				OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));
		response.getWriter().flush();
	}

	public T onErrorReturn(T ret) {
		return this.isErrorPresent() ? ret : this.result;
	}

	/**
	 * Convert Result into a HttpResponse.
	 *
	 * @return ResponseEntity for spring
	 */
	public ResponseEntity<Object> getHttpResponse() {
		Object response = this.isErrorPresent() ? this.getError().copyWithExtra(
				this.getExtra()).getBody() : this.getResult();

		return ResponseEntity.status(this.isErrorPresent() ? this.error.getStatus() : HttpStatus.OK.value())
				.header("Hash", hash(response)).body(response);
	}

	public boolean isResultPresent() {
		return this.getResult() != null;
	}

	public boolean isExtraPresent() {
		return this.getExtra() != null;
	}

	public boolean isErrorPresent() {
		return this.getError() != null;
	}

	public boolean isResultNotPresent() {
		return !this.isResultPresent();
	}

	public boolean isExtraNotPresent() {
		return !this.isExtraPresent();
	}

	public boolean isErrorNotPresent() {
		return !this.isErrorPresent();
	}

	public boolean isEmpty() {
		return this.getResult() == null && this.getError() == null && this.getExtra() == null;
	}

	public boolean isNotEmpty() {
		return !this.isEmpty();
	}

}
