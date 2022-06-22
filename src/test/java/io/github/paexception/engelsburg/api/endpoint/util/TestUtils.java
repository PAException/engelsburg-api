/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburg.api.endpoint.dto.ErrorDTO;
import io.github.paexception.engelsburg.api.util.Error;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Assert that a mvc result returned a specific error.
	 *
	 * @param result to check for error
	 * @param error  to check for in result
	 * @throws IOException if something goes wrong
	 */
	public static void assertThatIsError(MvcResult result, Error error) throws IOException {
		assertThat(
				((ErrorDTO) OBJECT_MAPPER.readerFor(ErrorDTO.class).readValue(
						result.getResponse().getContentAsString()
				)).isError(error)
		).isTrue();
	}

	public static void assertThatEmpty(MvcResult result) throws IOException {
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
		assertThat(result.getResponse().getContentAsString()).isEmpty();
	}

	public static RequestBuilder jsonRequest(MockHttpServletRequestBuilder builder, Object dto) throws IOException {
		return builder.contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(dto));
	}

	public static RequestBuilder jsonRequest(MockHttpServletRequestBuilder builder) {
		return builder.contentType(MediaType.APPLICATION_JSON);
	}

	public static <T> T parse(MvcResult result, Class<T> type) throws IOException {
		return OBJECT_MAPPER.readerFor(type).readValue(result.getResponse().getContentAsString());
	}
}
