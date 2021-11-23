package io.github.paexception.engelsburg.api.controller.internal;

import com.google.common.collect.Lists;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.ScopeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Controller for scopes.
 */
@Component
public class ScopeController implements UserDataHandler {

	private final ScopeRepository scopeRepository;

	public ScopeController(ScopeRepository scopeRepository) {
		this.scopeRepository = scopeRepository;
	}

	public static String mergeScopes(String[] value) {
		if (value == null || value.length == 0) return "";
		List<String> scopes = Lists.newArrayList(value);
		scopes.sort(String::compareToIgnoreCase);

		StringBuilder builder = new StringBuilder();
		int lastDepth = 0;

		for (int i = 0; i < scopes.size(); i++) {
			String scope = scopes.get(i);
			int currentDepth = 0;

			if (i != 0) {
				String[] split2 = scopes.get(i - 1).split("\\.");
				int lastIndex1 = 0;

				for (int j = 0; j < Math.min(StringUtils.countMatches(scope, "."), split2.length); j++) {
					lastIndex1 = j == 0 ? 0 : scope.indexOf(".", lastIndex1) + 1;
					String sub1 = scope.substring(lastIndex1);

					if (sub1.startsWith(split2[j])) {
						currentDepth++;
					} else {
						int depthDiff = lastDepth - split2.length + currentDepth - 2;

						if (depthDiff == 0) {
							builder.append("+");
						} else if (depthDiff > 0) {
							builder.append(".".repeat(depthDiff));
						} else {
							builder.append("-".repeat((depthDiff) * (-1)));
						}
						builder.append(sub1);
						break;
					}
				}
			} else {
				lastDepth = scope.split("\\.").length - 1;
				builder.append(scope);
			}
		}

		return builder.toString();
	}

	/**
	 * Get all scopes of a user.
	 *
	 * @param user to search for
	 * @return scopes as String[]
	 */
	public String[] getScopes(UserModel user) {
		return this.scopeRepository.findAllByUser(user).stream().map(ScopeModel::getScope).toArray(String[]::new);
	}

	/**
	 * Add a scope to a user.
	 *
	 * @param user  to search for
	 * @param scope to add
	 */
	public void addScope(UserModel user, String scope) {
		this.scopeRepository.save(new ScopeModel(-1, user, scope));
	}

	/**
	 * Update scopes of a user.
	 *
	 * @param user   to search for
	 * @param scopes to update if not existing
	 */
	public void updateScopes(UserModel user, List<String> scopes) {
		scopes.forEach(scope -> {
			if (!this.scopeRepository.existsByUserAndScope(user, scope))
				this.scopeRepository.save(new ScopeModel(-1, user, scope));
		});
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.scopeRepository.deleteAllByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.scopeRepository.findAllByUser(user));
	}

}
