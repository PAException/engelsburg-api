/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class NotificationPriorityTopicModel {
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int priorityTopicId;

	@NotBlank
	private String topic;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "notificationToken_notificationTokenId")
	private NotificationTokenModel notificationToken;

	public NotificationPriorityTopicModel(String topic, NotificationTokenModel notificationToken) {
		this.topic = topic;
		this.notificationToken = notificationToken;
	}
}
