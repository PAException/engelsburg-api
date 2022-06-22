/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Integer> {

	Optional<RefreshTokenModel> findByUser(UserModel user);

	Optional<RefreshTokenModel> findByToken(String refreshToken);

	List<RefreshTokenModel> findAllByUser(UserModel user);

	void deleteAllByUser(UserModel user);
}
