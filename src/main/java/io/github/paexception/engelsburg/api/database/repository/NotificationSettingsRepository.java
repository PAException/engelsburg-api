/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettingsModel, Integer> {

	Optional<NotificationSettingsModel> findByUser(UserModel user);

	boolean existsByUserAndEnabledAndByTimetable(UserModel user, boolean enabled, boolean byTimetable);

	List<NotificationSettingsModel> findAllByUser(UserModel user);
}
