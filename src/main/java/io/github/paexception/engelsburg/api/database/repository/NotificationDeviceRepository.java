/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDeviceRepository extends JpaRepository<NotificationDeviceModel, Integer> {

	List<NotificationDeviceModel> findAllByUser(UserModel user);

	void deleteByUserAndToken(UserModel user, String token);

	boolean existsByUserAndToken(UserModel user, String token);

	Optional<NotificationDeviceModel> findByUserAndToken(UserModel user, String token);
}
