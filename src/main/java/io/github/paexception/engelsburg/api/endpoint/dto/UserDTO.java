/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.spring.auth.AuthenticationInterceptor;
import org.apache.logging.log4j.util.Strings;

/**
 * UserDTO with critical and important user information.
 * Usually created by {@link AuthenticationInterceptor} while parsing the JWT.
 */
public class UserDTO {

	public final DecodedJWT jwt;
	public final UserModel user;
	private final String scopes;

	public UserDTO(DecodedJWT jwt, UserModel user) {
		this.jwt = jwt;
		this.user = user;
		this.scopes = jwt != null ? jwt.getClaim("scopes").asString() : Strings.EMPTY;
	}

	/**
	 * Check if scopes contains a single scope.
	 * Function doesn't decode scopes, just searches through.
	 *
	 * @param scope to check for
	 * @return true if scope was found
	 */
	public boolean hasScope(String scope) {
		return ScopeController.hasScope(scope, this.scopes);
	}

	public boolean is(UserModel user) {
		return this.user.equals(user);
	}
}
