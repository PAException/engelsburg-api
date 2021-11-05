package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class UserModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int incUserId;

	@NotNull
	@Column(length = 16, unique = true)
	private UUID userId;
	@Email
	@Column(unique = true)
	@NotBlank
	private String email;
	@NotNull
	@Column(length = 32)
	private byte[] password;
	@NotBlank
	private String salt;
	private boolean verified;

	public boolean is(UserDTO userDTO) {
		return this.equals(userDTO.user);
	}

}
