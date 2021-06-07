package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.NotificationController;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.spring.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
public class NotificationEndpoint {

	@Autowired
	private NotificationController notificationController;

	/**
	 * Change user notification
	 *
	 * @see NotificationController#changeNotificationSettings(ChangeNotificationSettingsRequestDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@PostMapping("/user/notification")
	public Object changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequestDTO dto, DecodedJWT jwt) {
		return this.notificationController.changeNotificationSettings(dto, jwt).getHttpResponse();
	}

	/**
	 * Add a notification device
	 *
	 * @see NotificationController#addNotificationDevice(NotificationDeviceDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@PostMapping("/user/notification/device")
	public Object addNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, DecodedJWT jwt) {
		return this.notificationController.addNotificationDevice(dto, jwt).getHttpResponse();
	}

	/**
	 * Remove a notification device
	 *
	 * @see NotificationController#removeNotificationDevice(NotificationDeviceDTO, DecodedJWT)
	 */
	@AuthScope("notification.settings.write.self")
	@DeleteMapping("/user/notification/device")
	public Object removeNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, DecodedJWT jwt) {
		return this.notificationController.removeNotificationDevice(dto, jwt).getHttpResponse();
	}

}
