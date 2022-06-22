/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationDeviceRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDeviceDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.util.Constants.NotificationDevice.NAME_KEY;

/**
 * Controller to handle notificationDevices of users.
 */
@Component
@AllArgsConstructor
public class NotificationDeviceController {

	private final NotificationDeviceRepository notificationDeviceRepository;

	/**
	 * Add a new device to receive notifications on.
	 *
	 * @param dto     with device token and langCode
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> addNotificationDevice(NotificationDeviceDTO dto, UserDTO userDTO) {
		//Check if notification device already exists, then return error
		if (this.notificationDeviceRepository.existsByUserAndToken(userDTO.user, dto.getToken()))
			return Result.of(Error.ALREADY_EXISTS, NAME_KEY);

		//Save new notificationDevice, return empty result
		this.notificationDeviceRepository.save(
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
		//Get optional notification device by user and token, if not found return error
		Optional<NotificationDeviceModel> optionalDevice = this.notificationDeviceRepository
				.findByUserAndToken(userDTO.user, dto.getToken());
		if (optionalDevice.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Update notificationDevice, return empty result
		this.notificationDeviceRepository.save(optionalDevice.get().updateLangCode(dto.getLangCode()));
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
		//Check if notificationDevice exists, otherwise return error
		if (!this.notificationDeviceRepository.existsByUserAndToken(userDTO.user, dto.getToken()))
			return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Delete notificationDevice, return empty result
		this.notificationDeviceRepository.deleteByUserAndToken(userDTO.user, dto.getToken());
		return Result.empty();
	}

	/**
	 * Get notificationDevices of user.
	 *
	 * @param userId To get notificationDevice from
	 * @return List of notificationDevices
	 */
	public List<NotificationDeviceModel> get(UserModel userId) {
		return this.notificationDeviceRepository.findAllByUser(userId);
	}
}
