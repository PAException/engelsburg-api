package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.response.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Data
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
	@NotBlank
	private String password;
	@NotBlank
	private String salt;
	private boolean verified;

	public UserResponseDTO toResponseDTO() {
		return new UserResponseDTO(
				this.userId,
				this.email,
				this.password,
				this.salt,
				this.verified
		);
	}

}
