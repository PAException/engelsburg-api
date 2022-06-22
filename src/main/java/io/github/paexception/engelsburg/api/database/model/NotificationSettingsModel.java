/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class NotificationSettingsModel {

	@Setter(AccessLevel.PRIVATE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationSettingId;

	@NotNull
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "user_userId")
	private UserModel user;

	private boolean enabled;
	private boolean byTimetable;

	public static NotificationSettingsModel template(UserDTO userDTO) {
		NotificationSettingsModel notificationSettings = new NotificationSettingsModel();
		notificationSettings.setNotificationSettingId(-1);
		notificationSettings.setUser(userDTO.user);

		return notificationSettings;
	}

	public NotificationSettingsDTO toResponseDTO() {
		return new NotificationSettingsDTO(
				this.enabled,
				this.byTimetable
		);
	}
}
