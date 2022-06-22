/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationSettingsRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.NotificationSettings.NAME_KEY;

/**
 * Controller for notifications.
 */
@Component
@AllArgsConstructor
public class NotificationSettingsController {

	private final NotificationSettingsRepository notificationSettingsRepository;
	private final NotificationDeviceController notificationDeviceController;

	/**
	 * Change notification settings of user.
	 *
	 * @param dto     with all notification settings
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> changeNotificationSettings(ChangeNotificationSettingsRequestDTO dto, UserDTO userDTO) {
		//Get optional notificationSettings of user, if empty create new
		Optional<NotificationSettingsModel> optionalNotificationSettings = this.notificationSettingsRepository
				.findByUser(userDTO.user);
		NotificationSettingsModel notificationSettings = optionalNotificationSettings
				.orElse(NotificationSettingsModel.template(userDTO));

		//Set properties of notificationSettings
		notificationSettings.setEnabled(dto.isEnabled());
		notificationSettings.setByTimetable(dto.isByTimetable());

		//Save updated or newly create notificationSettings, return empty result
		this.notificationSettingsRepository.save(notificationSettings);
		return Result.empty();
	}

	/**
	 * Get notification settings of user.
	 *
	 * @param userDTO user information
	 * @return notification settings
	 */
	public Result<NotificationSettingsDTO> getNotificationSettings(UserDTO userDTO) {
		//Get optional notificationSettings of user, return dto, if not found return error
		return this.notificationSettingsRepository.findByUser(userDTO.user)
				.map(notificationSettings -> Result.of(notificationSettings.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Fetch all notification devices of users.
	 *
	 * @param users stream of users to fetch for
	 * @return a Set of notification devices
	 */
	@Transactional
	public Set<NotificationDeviceModel> getTimetableNotificationDeviceOfUsers(Stream<UserModel> users) {
		return users
				//Get only users which have enabled notification by timetable
				.filter(userId -> this.notificationSettingsRepository
						.existsByUserAndEnabledAndByTimetable(userId, true, true))
				//Get all notification devices of those
				.flatMap(userId -> this.notificationDeviceController.get(userId).stream())
				.collect(Collectors.toSet());
	}
}
