package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettingsModel, Integer> {

	Optional<NotificationSettingsModel> findByUser(UserModel user);

	void deleteByUser(UserModel user);

	boolean existsByUserAndEnabledAndByTimetable(UserModel user, boolean enabled, boolean byTimetable);

}
