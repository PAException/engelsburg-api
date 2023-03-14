/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationSettingsController;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for notificationSettings.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user/notification")
public class NotificationSettingsEndpoint {

	private final NotificationSettingsController notificationSettingsController;

	/**
	 * Change user notification settings.
	 *
	 * @see NotificationSettingsController#changeNotificationSettings(ChangeNotificationSettingsRequestDTO, UserDTO)
	 */
	@AuthScope("notification.settings.write.self")
	@PatchMapping
	@Response
	public Object changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequestDTO dto,
			UserDTO userDTO) {
		return this.notificationSettingsController.changeNotificationSettings(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get notification settings.
	 *
	 * @see NotificationSettingsController#getNotificationSettings(UserDTO)
	 */
	@AuthScope("notification.settings.read.self")
	@GetMapping
	@Response(NotificationSettingsDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "notification_settings")
	public Object getNotificationSettings(UserDTO userDTO) {
		return this.notificationSettingsController.getNotificationSettings(userDTO).getHttpResponse();
	}
}
