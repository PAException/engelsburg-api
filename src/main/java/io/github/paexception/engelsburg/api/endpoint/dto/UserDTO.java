package io.github.paexception.engelsburg.api.endpoint.dto;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.spring.auth.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
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
		String[] split = scope.split("\\.");
		String sub = this.scopes;
		boolean found = true;
		for (int i = 0; i < split.length; i++) {
			String part = split[i];
			if (sub.contains(part)) {
				int index = sub.indexOf(part);
				String pre = sub.substring(0, index);
				if (StringUtils.countMatches(pre, ".") != StringUtils.countMatches(pre, "-") - (i == 0 ? 0 : -1)) {
					found = false;
				}
				char c = sub.charAt(index + part.length());
				if (c != '+' && c != '.' && c != '-') {
					found = false;
				}
				sub = sub.substring(sub.indexOf(part));
			} else {
				found = false;
			}
		}

		return found;
	}
}
