/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenModel, Integer> {

	default Optional<TokenModel> findByTypeAndTokenWithParams(String type, String token) {
		return this.findByTypeAndTokenIsLike(type, token + ",%");
	}

	Optional<TokenModel> findByTypeAndTokenIsLike(String type, String token);

	default void deleteByUserAndTypeAndTokenWithParams(UserModel user, String type, String token) {
		this.deleteByUserAndTypeAndTokenIsLike(user, type, token + ",%");
	}

	void deleteByUserAndTypeAndTokenIsLike(UserModel user, String type, String token);

	List<TokenModel> findAllByUser(UserModel user);

	default boolean existsByTokenWithParams(String token) {
		return this.existsByTokenIsLike(token + ",%");
	}

	boolean existsByTokenIsLike(String token);
}
