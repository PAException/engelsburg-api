package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationDeviceRepository;
import io.github.paexception.engelsburg.api.database.repository.NotificationSettingsRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.Notification.NAME_KEY;

/**
 * Controller for notifications.
 */
@Component
public class NotificationController implements UserDataHandler {

	private final NotificationSettingsRepository notificationSettingsRepository;
	private final NotificationDeviceRepository notificationDeviceRepository;

	public NotificationController(
			NotificationDeviceRepository notificationDeviceRepository,
			NotificationSettingsRepository notificationSettingsRepository) {
		this.notificationDeviceRepository = notificationDeviceRepository;
		this.notificationSettingsRepository = notificationSettingsRepository;
	}

	/**
	 * Change notification settings of user.
	 *
	 * @param dto     with all notification settings
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> changeNotificationSettings(ChangeNotificationSettingsRequestDTO dto, UserDTO userDTO) {
		Optional<NotificationSettingsModel> optionalNotificationSettings = this.notificationSettingsRepository
				.findByUser(userDTO.user);
		NotificationSettingsModel notificationSettings;
		if (optionalNotificationSettings.isPresent()) notificationSettings = optionalNotificationSettings.get();
		else {
			notificationSettings = new NotificationSettingsModel();
			notificationSettings.setNotificationSettingId(-1);
			notificationSettings.setUser(userDTO.user);
		}

		notificationSettings.setEnabled(dto.isEnabled());
		notificationSettings.setByTimetable(dto.isByTimetable());

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
		return this.notificationSettingsRepository.findByUser(userDTO.user)
				.map(notificationSettings -> Result.of(notificationSettings.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Add a new device to receive notifications on.
	 *
	 * @param dto     with device token and langCode
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> addNotificationDevice(NotificationDeviceDTO dto, UserDTO userDTO) {
		if (this.notificationDeviceRepository.existsByUserAndToken(userDTO.user, dto.getToken()))
			return Result.of(Error.ALREADY_EXISTS, NAME_KEY + "_device");
		else this.notificationDeviceRepository.save(
				new NotificationDeviceModel(-1, userDTO.user, dto.getToken(), dto.getLangCode()));

		return Result.empty();
	}

	/**
	 * Update langCode of notification device.
	 *
	 * @param dto     with device token and langCode
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> updateNotificationDevice(NotificationDeviceDTO dto, UserDTO userDTO) {
		Optional<NotificationDeviceModel> optionalDevice = this.notificationDeviceRepository
				.findByUserAndToken(userDTO.user, dto.getToken());
		if (optionalDevice.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY + "_device");
		else this.notificationDeviceRepository.save(optionalDevice.get().updateLangCode(dto.getLangCode()));

		return Result.empty();
	}

	/**
	 * Remove an existing device which receives notifications.
	 *
	 * @param dto     with device token and langCode
	 * @param userDTO user information
	 * @return empty result or error
	 */
	@Transactional
	public Result<?> removeNotificationDevice(NotificationDeviceDTO dto, UserDTO userDTO) {
		if (!this.notificationDeviceRepository.existsByUserAndToken(userDTO.user, dto.getToken()))
			return Result.of(Error.NOT_FOUND, NAME_KEY + "_device");
		else this.notificationDeviceRepository.deleteByUserAndToken(userDTO.user, dto.getToken());

		return Result.empty();
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.notificationDeviceRepository.deleteAllByUser(user);
		this.notificationSettingsRepository.deleteByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.notificationDeviceRepository.findAllByUser(user),
				this.notificationSettingsRepository.findByUser(user));
	}

	/**
	 * Fetch all notification devices of users.
	 *
	 * @param users stream of users to fetch for
	 * @return a Set of notification devices
	 */
	@Transactional
	public Set<NotificationDeviceModel> getTimetableNotificationDeviceOfUsers(Stream<UserModel> users) {
		return users.filter(
						userId -> this.notificationSettingsRepository.existsByUserAndEnabledAndByTimetable(userId, true,
								true))
				.flatMap(userId -> this.notificationDeviceRepository.findAllByUser(userId).stream()).collect(
						Collectors.toSet());
	}
}
