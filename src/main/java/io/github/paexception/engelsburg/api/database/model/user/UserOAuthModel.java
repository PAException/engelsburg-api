/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.util.maskjson.MaskJson;
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
public class UserOAuthModel {

	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userOAuthId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_userId")
	private UserModel user;

	@NotBlank
	private String service; //e.g. google
	@NotBlank
	@MaskJson
	private String identification; //service specific
	@NotBlank
	private String name; //e.g. some email
}
