/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationTokenModel, Integer> {
	Optional<NotificationTokenModel> findByToken(String token);

	void deleteAllByTokenIn(List<String> tokens);
}
