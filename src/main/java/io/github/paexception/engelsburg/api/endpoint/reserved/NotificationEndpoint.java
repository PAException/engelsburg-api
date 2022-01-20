package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationController;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for notification actions.
 */
@RestController
public class NotificationEndpoint {

	private final NotificationController notificationController;

	public NotificationEndpoint(
			NotificationController notificationController) {
		this.notificationController = notificationController;
	}

	/**
	 * Change user notification settings.
	 *
	 * @see NotificationController#changeNotificationSettings(ChangeNotificationSettingsRequestDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PatchMapping("/user/notification")
	public Object changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequestDTO dto,
			UserDTO userDTO) {
		return this.notificationController.changeNotificationSettings(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get notification settings.
	 *
	 * @see NotificationController#getNotificationSettings(UserDTO)
	 */
	@AuthScope("notification.settings.read.self")
	@GetMapping("/user/notification")
	public Object getNotificationSettings(UserDTO userDTO) {
		return this.notificationController.getNotificationSettings(userDTO).getHttpResponse();
	}

	/**
	 * Add a notification device.
	 *
	 * @see NotificationController#addNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PostMapping("/user/notification/device")
	public Object addNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationController.addNotificationDevice(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update language of a notification device.
	 *
	 * @see NotificationController#updateNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PatchMapping("/user/notification/device")
	public Object updateNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationController.updateNotificationDevice(dto, userDTO).getHttpResponse();
	}

	/**
	 * Remove a notification device.
	 *
	 * @see NotificationController#removeNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@DeleteMapping("/user/notification/device")
	public Object removeNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationController.removeNotificationDevice(dto, userDTO).getHttpResponse();
	}

}
