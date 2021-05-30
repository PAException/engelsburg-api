package io.github.paexception.engelsburg.api.authentication;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.repository.ScopeRepository;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Controller for scopes
 */
@Component
public class ScopeController implements UserDataHandler {

	@Autowired
	private ScopeRepository scopeRepository;

	public String[] getScopes(UUID userId) {
		return this.scopeRepository.findAllByUserId(userId).stream().map(ScopeModel::getScope).toArray(String[]::new);
	}

	public void addScope(UUID userId, String scope) {
		this.scopeRepository.save(new ScopeModel(-1, userId, scope));
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.scopeRepository.deleteAllByUserId(userId);
	}

	@Override
	public Result<?> getUserData(UUID userId) {
		return Result.of(this.scopeRepository.findAllByUserId(userId));
	}

}
