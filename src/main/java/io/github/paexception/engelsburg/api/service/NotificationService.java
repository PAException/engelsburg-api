package io.github.paexception.engelsburg.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.TimetableController;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationSettingsRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static io.github.paexception.engelsburg.api.util.Constants.Notification.NAME_KEY;

@Component
public class NotificationService implements UserDataHandler {

	@Autowired
	private NotificationSettingsRepository notificationSettingsRepository;
	@Autowired
	private TimetableController timetableController;

	public void sendSubstituteNotifications(List<SubstituteDTO> dtos) {
		//TODO
	}

	public Result<?> changeNotificationSettings(ChangeNotificationSettingsRequestDTO dto, DecodedJWT jwt) {
		Optional<NotificationSettingsModel> optionalNotificationSettings = this.notificationSettingsRepository
				.findByUserId(UUID.fromString(jwt.getSubject()));
		NotificationSettingsModel notificationSettings;
		if (optionalNotificationSettings.isPresent()) notificationSettings = optionalNotificationSettings.get();
		else {
			notificationSettings = new NotificationSettingsModel();
			notificationSettings.setNotificationSettingId(-1);
			notificationSettings.setUserId(UUID.fromString(jwt.getSubject()));
		}

		notificationSettings.setEnabled(dto.isEnabled());
		notificationSettings.setByClass(dto.isByClass());
		if (dto.isByClass() && dto.getClassName().isBlank()) Result.of(Error.MISSING_PARAM, NAME_KEY);
		else notificationSettings.setClassName(dto.getClassName());
		notificationSettings.setByTeacher(dto.isByTeacher());
		if (dto.isByTeacher() && dto.getTeacherAbbreviation().isBlank()) Result.of(Error.MISSING_PARAM, NAME_KEY);
		else notificationSettings.setTeacherAbbreviation(dto.getTeacherAbbreviation());
		notificationSettings.setByTimetable(dto.isByTimetable());

		this.notificationSettingsRepository.save(notificationSettings);

		return Result.empty();
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.notificationSettingsRepository.deleteByUserId(userId);
	}

	@Override
	public Result<?> getUserData(UUID userId) {
		return Result.of(this.notificationSettingsRepository.findByUserId(userId));
	}

}
