/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository.user;

import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserPasswordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPasswordModel, Integer> {

	Optional<UserPasswordModel> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<UserPasswordModel> findByUser(UserModel user);
}
