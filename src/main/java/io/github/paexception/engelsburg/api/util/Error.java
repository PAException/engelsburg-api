/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Error class to return errors on RestControllers in a solid http response,
 * be it a not found element or an internal server error.
 */
@Getter
public class Error extends ResponseEntity<Object> {

	public static final Error FORBIDDEN = new Error(HttpStatus.FORBIDDEN, I18n.FORBIDDEN);
	public static final Error NOT_FOUND = new Error(HttpStatus.NOT_FOUND, I18n.NOT_FOUND);
	public static final Error INTERNAL_SERVER_ERROR = new Error(HttpStatus.INTERNAL_SERVER_ERROR,
			I18n.INTERNAL_SERVER_ERROR);
	public static final Error UNAUTHORIZED = new Error(HttpStatus.UNAUTHORIZED, I18n.UNAUTHORIZED);
	public static final Error ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, I18n.ALREADY_EXISTS);
	public static final Error NOT_MODIFIED = new Error(HttpStatus.NOT_MODIFIED, I18n.NOT_MODIFIED);
	public static final Error INVALID_PARAM = new Error(HttpStatus.BAD_REQUEST, I18n.INVALID_PARAM);
	public static final Error BAD_REQUEST = new Error(HttpStatus.BAD_REQUEST, I18n.BAD_REQUEST);
	public static final Error EXPIRED = new Error(HttpStatus.BAD_REQUEST, I18n.EXPIRED);
	public static final Error FAILED = new Error(HttpStatus.BAD_REQUEST, I18n.FAILED);
	public static final Error INVALID = new Error(HttpStatus.BAD_REQUEST, I18n.INVALID);
	public static final Error TOO_MANY_REQUESTS = new Error(HttpStatus.TOO_MANY_REQUESTS, I18n.TOO_MANY_REQUESTS);
	public static final Error FAILED_DEPENDENCY = new Error(HttpStatus.FAILED_DEPENDENCY, I18n.FAILED_DEPENDENCY);

	private final int status;
	private final String messageKey;
	private final String extra;

	public Error(HttpStatus status, String messageKey) {
		this(status, messageKey, null);
	}

	private Error(HttpStatus status, String messageKey, String extra) {
		super(status);
		this.status = status.value();
		this.messageKey = messageKey;
		this.extra = extra;
	}

	public static Error fromHttpStatus(HttpStatus status) {
		return fromHttpStatus(status, null);
	}

	public static Error fromHttpStatus(HttpStatus status, String extra) {
		return new Error(status, status.getReasonPhrase().toUpperCase().replace(" ", "_"), extra);
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Error copyWithExtra(String extra) {
		return new Error(HttpStatus.resolve(this.getStatus()), this.getMessageKey(), extra);
	}

	@Data
	private static class HttpResponse {
		private final int status;
		private final String messageKey;
		private final String extra;
	}

	@Override
	public Object getBody() {
		// We want to control the http status code for errors, so we extend the ResponseEntity and set a custom body
		return new HttpResponse(this.getStatus(), this.getMessageKey(), this.getExtra());
	}

}
