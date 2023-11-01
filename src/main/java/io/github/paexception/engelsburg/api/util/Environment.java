/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

/**
 * Class to get any environment variable.
 */
public class Environment {

	public static final String GOOGLE_ACCOUNT_CREDENTIALS = System.getenv("GOOGLE_ACCOUNT_CREDENTIALS");
	public static final String SCHOOL_TOKEN = System.getenv("SCHOOL_TOKEN");
	public static final boolean PRODUCTION = Boolean.parseBoolean(System.getenv("PRODUCTION"));
	public static final boolean BLURHASH = Boolean.parseBoolean(System.getenv("BLURHASH"));

}
