/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Internationalization of error messages.
 * Used for slightly customized errors.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I18n {

	public static final String NOT_FOUND = "NOT_FOUND";
	public static final String INVALID_PARAM = "INVALID_PARAM";
	public static final String UNAUTHORIZED = "UNAUTHORIZED";
	public static final String FORBIDDEN = "FORBIDDEN";
	public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
	public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
	public static final String NOT_MODIFIED = "NOT_MODIFIED";
	public static final String EXPIRED = "EXPIRED";
	public static final String FAILED = "FAILED";
	public static final String INVALID = "INVALID";
	public static final String BAD_REQUEST = "BAD_REQUEST";
	public static final String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
	public static final String FAILED_DEPENDENCY = "FAILED_DEPENDENCY";
}
