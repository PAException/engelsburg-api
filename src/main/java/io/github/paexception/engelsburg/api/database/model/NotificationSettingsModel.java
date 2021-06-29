package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class NotificationSettingsModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationSettingId;

	@NotNull
	@Column(length = 16, unique = true)
	private UUID userId;
	private boolean enabled;
	private boolean byTimetable;

	public NotificationSettingsDTO toResponseDTO() {
		return new NotificationSettingsDTO(
				this.enabled,
				this.byTimetable
		);
	}

}
