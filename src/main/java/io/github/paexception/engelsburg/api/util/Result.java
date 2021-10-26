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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

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
	private static MessageDigest digest;
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
	 * Maps the the given instance to an instance with other generics.
	 *
	 * @param <T>      The new generics
	 * @param instance The instance to map
	 * @return the given instance
	 */
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
		try {
			if (digest == null) digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ignored) {
		}
		if (o == null) return null;

		return bytesToHex(digest.digest(o.toString().getBytes()));
	}

	/**
	 * Function to convert a bytearray into a hex string.
	 *
	 * @param hash bytearray to convert to hex string
	 * @return hex string
	 */
	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
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
		response.getWriter().write(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));
		response.getWriter().flush();
	}

	/**
	 * Convert Result into a HttpResponse.
	 *
	 * @return ResponseEntity for spring
	 */
	public ResponseEntity<Object> getHttpResponse() {
		Object response = this.isErrorPresent() ? this.getError().copyWithExtra(this.getExtra()).getBody() : this.getResult();

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
