/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository.user;

import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.model.user.UserOAuthModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserOAuthRepository extends JpaRepository<UserOAuthModel, Integer> {

	List<UserOAuthModel> findAllByUser(UserModel user);

	boolean existsByServiceAndIdentification(String identification, String service);

	Optional<UserOAuthModel> findByServiceAndIdentification(String service, String identification);

	Optional<UserOAuthModel> findByUserAndService(UserModel user, String service);
}
