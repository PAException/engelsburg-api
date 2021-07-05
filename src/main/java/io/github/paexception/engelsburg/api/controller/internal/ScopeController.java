package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

/**
 * Controller for scopes.
 */
@Component
public class ScopeController implements UserDataHandler {

	@Autowired
	private ScopeRepository scopeRepository;

	/**
	 * Get all scopes of a user.
	 *
	 * @param userId to search for
	 * @return scopes as String[]
	 */
	public String[] getScopes(UUID userId) {
		return this.scopeRepository.findAllByUserId(userId).stream().map(ScopeModel::getScope).toArray(String[]::new);
	}

	/**
	 * Add a scope to a user.
	 *
	 * @param userId to search for
	 * @param scope  to add
	 */
	public void addScope(UUID userId, String scope) {
		this.scopeRepository.save(new ScopeModel(-1, userId, scope));
	}

	/**
	 * Update scopes of a user.
	 *
	 * @param user   to search for
	 * @param scopes to update if not existing
	 */
	public void updateScopes(UserModel user, List<String> scopes) {
		scopes.forEach(scope -> {
			if (!this.scopeRepository.existsByUserIdAndScope(user.getUserId(), scope))
				this.scopeRepository.save(new ScopeModel(-1, user.getUserId(), scope));
		});
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.scopeRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.scopeRepository.findAllByUserId(userId));
	}

}
