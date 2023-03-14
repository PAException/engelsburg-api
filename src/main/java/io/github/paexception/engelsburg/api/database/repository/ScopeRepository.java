/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScopeRepository extends JpaRepository<ScopeModel, Integer> {

	List<ScopeModel> findAllByUser(UserModel user);

	boolean existsByUserAndScope(UserModel user, String scope);
}
