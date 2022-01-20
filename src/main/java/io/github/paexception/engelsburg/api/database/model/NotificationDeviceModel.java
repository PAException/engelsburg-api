package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class NotificationDeviceModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationDeviceId;

	@JsonIgnore
	@ManyToOne
	private UserModel user;
	@NotBlank
	private String token;
	@NotBlank
	private String langCode;

	public NotificationDeviceModel updateLangCode(String langCode) {
		this.langCode = langCode;

		return this;
	}
}
