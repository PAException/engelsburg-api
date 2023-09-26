/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationSettingsController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Controller to handle notification settings of FCM-Clients.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/settings/notification")
public class NotificationSettingsEndpoint {

	private final NotificationSettingsController notificationSettingsController;

	/**
	 * Update the notification settings of an FCM-token.
	 *
	 * @see NotificationSettingsController#updateNotificationSettings(UpdateNotificationSettingsRequestDTO)
	 */
	@PostMapping
	@Response
	public Object updateNotificationSettings(@RequestBody @Valid UpdateNotificationSettingsRequestDTO dto) {
		return this.notificationSettingsController.updateNotificationSettings(dto).getHttpResponse();
	}

	/**
	 * Delete the notification settings of an FCM-token.
	 *
	 * @see NotificationSettingsController#deleteNotificationSettings(String)
	 */
	@DeleteMapping
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "notification_settings")
	public Object deleteNotificationSettings(@RequestParam @NotBlank String token) {
		return this.notificationSettingsController.deleteNotificationSettings(token).getHttpResponse();
	}
}
