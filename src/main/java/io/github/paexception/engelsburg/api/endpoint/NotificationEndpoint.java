package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.NotificationController;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private NotificationController notificationController;

	/**
	 * Change user notification settings.
	 *
	 * @see NotificationController#changeNotificationSettings(ChangeNotificationSettingsRequestDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@PatchMapping("/user/notification")
	public Object changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequestDTO dto, DecodedJWT jwt) {
		return this.notificationController.changeNotificationSettings(dto, jwt).getHttpResponse();
	}

	/**
	 * Get notification settings.
	 *
	 * @see NotificationController#getNotificationSettings(DecodedJWT)
	 */
	@AuthScope("notification.settings.read.self")
	@GetMapping("/user/notification")
	public Object getNotificationSettings(DecodedJWT jwt) {
		return this.notificationController.getNotificationSettings(jwt).getHttpResponse();
	}

	/**
	 * Add a notification device.
	 *
	 * @see NotificationController#addNotificationDevice(NotificationDeviceDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@PostMapping("/user/notification/device")
	public Object addNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, DecodedJWT jwt) {
		return this.notificationController.addNotificationDevice(dto, jwt).getHttpResponse();
	}

	/**
	 * Remove a notification device.
	 *
	 * @see NotificationController#removeNotificationDevice(NotificationDeviceDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@DeleteMapping("/user/notification/device")
	public Object removeNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, DecodedJWT jwt) {
		return this.notificationController.removeNotificationDevice(dto, jwt).getHttpResponse();
	}

}
