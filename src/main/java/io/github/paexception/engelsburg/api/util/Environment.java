package io.github.paexception.engelsburg.api.util;

public class Environment {

	public static final String JWT_SECRET = System.getenv("JWT_SECRET");
	public static final String GOOGLE_ACCOUNT_CREDENTIALS = System.getenv("GOOGLE_ACCOUNT_CREDENTIALS");
	public static final String SCHOOL_TOKEN = System.getenv("SCHOOL_TOKEN");
	public static final boolean PRODUCTION = Boolean.parseBoolean(System.getenv("PRODUCTION"));
	public static final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
	public static final String GOOGLE_CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
	public static final String OAUTH2_REDIRECT_URI = System.getenv("OAUTH2_REDIRECT_URI");

}
