package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationDeviceRepository;
import io.github.paexception.engelsburg.api.database.repository.NotificationSettingsRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.Notification.NAME_KEY;

/**
 * Controller for notifications
 */
@Component
public class NotificationController implements UserDataHandler {

	@Autowired
	private NotificationSettingsRepository notificationSettingsRepository;
	@Autowired
	private NotificationDeviceRepository notificationDeviceRepository;

	/**
	 * Change notification settings of user
	 *
	 * @param dto with all notification settings
	 * @param jwt with userId
	 * @return empty result or error
	 */
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
		notificationSettings.setByTimetable(dto.isByTimetable());

		this.notificationSettingsRepository.save(notificationSettings);

		return Result.empty();
	}

	/**
	 * Get notification settings of user
	 *
	 * @param jwt with userId
	 * @return notification settings
	 */
	public Result<NotificationSettingsDTO> getNotificationSettings(DecodedJWT jwt) {
		return this.notificationSettingsRepository.findByUserId(UUID.fromString(jwt.getSubject()))
				.map(notificationSettings -> Result.of(notificationSettings.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Add a new device to receive notifications on
	 *
	 * @param dto with device token
	 * @param jwt with userId
	 * @return empty result or error
	 */
	public Result<?> addNotificationDevice(NotificationDeviceDTO dto, DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		if (this.notificationDeviceRepository.existsByUserIdAndToken(userId, dto.getToken()))
			return Result.of(Error.ALREADY_EXISTS, NAME_KEY + "_device");
		else this.notificationDeviceRepository.save(new NotificationDeviceModel(-1, userId, dto.getToken()));

		return Result.empty();
	}

	/**
	 * Remove an existing device which receives notifications
	 *
	 * @param dto with device token
	 * @param jwt with userId
	 * @return empty result or error
	 */
	public Result<?> removeNotificationDevice(NotificationDeviceDTO dto, DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		if (!this.notificationDeviceRepository.existsByUserIdAndToken(userId, dto.getToken()))
			return Result.of(Error.NOT_FOUND, NAME_KEY + "_device");
		else this.notificationDeviceRepository.deleteByUserIdAndToken(userId, dto.getToken());

		return Result.empty();
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.notificationDeviceRepository.deleteAllByUserId(userId);
		this.notificationSettingsRepository.deleteByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.notificationDeviceRepository.findAllByUserId(userId),
				this.notificationSettingsRepository.findByUserId(userId));
	}

	/**
	 * Fetch all device tokens of userIds
	 *
	 * @param userIds Set of userIds to fetch for
	 * @return a Set of device tokens
	 */
	@Transactional
	public Set<String> getTimetableNotificationTokensOfUsers(Stream<UUID> userIds) {
		return userIds.filter(userId -> this.notificationSettingsRepository.existsByUserIdAndEnabledAndByTimetable(userId, true, true))
				.flatMap(userId -> this.notificationDeviceRepository.findAllByUserId(userId).stream()
						.map(NotificationDeviceModel::getToken)).collect(Collectors.toSet());
	}

}
