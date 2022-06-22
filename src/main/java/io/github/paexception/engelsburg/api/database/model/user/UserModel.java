/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.NotificationSettingsModel;
import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@NoArgsConstructor
@Entity
@Table
public class UserModel {

	@Getter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int incUserId;
	@NotNull
	@Column(length = 16, unique = true)
	private UUID userId;
	@Setter
	private boolean verified;
	@Setter
	@NotBlank
	private String username; //Name to show in app (or could be email)

	@Setter
	@Nullable
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "semesterId")
	@OneToOne
	private SemesterModel currentSemester;


	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private UserPasswordModel userPassword;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<UserOAuthModel> userOAuths;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<ArticleSaveModel> articleSaves;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<ScopeModel> scopes;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<RefreshTokenModel> refreshTokens;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<TokenModel> tokens;
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private NotificationSettingsModel notificationSettings;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<NotificationDeviceModel> notificationDevices;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<SemesterModel> semester;

	public UserModel(int incUserId, UUID userId, boolean verified, String username) {
		this.incUserId = incUserId;
		this.userId = userId;
		this.verified = verified;
		this.username = username;
	}

	public boolean is(UserDTO userDTO) {
		return this.equals(userDTO.user);
	}
}
