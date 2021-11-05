package io.github.paexception.engelsburg.api.database.model;

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

	@ManyToOne
	private UserModel user;
	private String token;

}
