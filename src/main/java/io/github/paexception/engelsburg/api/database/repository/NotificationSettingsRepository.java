package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettingsModel, Integer> {

	Optional<NotificationSettingsModel> findByUserId(UUID userId);

	void deleteByUserId(UUID userId);

	Stream<NotificationSettingsModel> findAllByEnabledAndByClassAndClassName(boolean enabled, boolean byClass, String className);

	Stream<NotificationSettingsModel> findAllByEnabledAndByTeacherAndTeacherAbbreviation(boolean enabled, boolean byTeacher, String teacherAbbreviation);

}
