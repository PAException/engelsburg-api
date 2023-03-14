/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationDeviceController;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController to handle notificationDevices.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user/notification/device")
public class NotificationDeviceEndpoint {

	private final NotificationDeviceController notificationDeviceController;

	/**
	 * Add a notification device.
	 *
	 * @see NotificationDeviceController#addNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PostMapping
	@Response
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "notification_device")
	public Object addNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationDeviceController.addNotificationDevice(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update language of a notification device.
	 *
	 * @see NotificationDeviceController#updateNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PatchMapping
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "notification_device")
	public Object updateNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationDeviceController.updateNotificationDevice(dto, userDTO).getHttpResponse();
	}

	/**
	 * Remove a notification device.
	 *
	 * @see NotificationDeviceController#removeNotificationDevice(NotificationDeviceDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@DeleteMapping
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "notification_device")
	public Object removeNotificationDevice(@RequestBody @Valid NotificationDeviceDTO dto, UserDTO userDTO) {
		return this.notificationDeviceController.removeNotificationDevice(dto, userDTO).getHttpResponse();
	}
}
