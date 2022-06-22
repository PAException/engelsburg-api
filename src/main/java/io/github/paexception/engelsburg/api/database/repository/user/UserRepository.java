/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository.user;

import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

	UserModel findByUserId(UUID userId);

	List<UserModel> findAllByVerified(boolean verified);
}
