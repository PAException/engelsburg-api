package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationSettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class NotificationSettingsModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationSettingId;

	@JsonIgnore
	@OneToOne
	private UserModel user;

	private boolean enabled;
	private boolean byTimetable;

	public NotificationSettingsDTO toResponseDTO() {
		return new NotificationSettingsDTO(
				this.enabled,
				this.byTimetable
		);
	}

}
