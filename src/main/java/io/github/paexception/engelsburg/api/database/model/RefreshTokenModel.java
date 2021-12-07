package io.github.paexception.engelsburg.api.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class RefreshTokenModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tokenId;
	@OneToOne
	private UserModel user;
	@NotBlank
	@Column(unique = true)
	private String token;
	private long expire;

	public RefreshTokenModel(UserModel user) {
		this.user = user;
	}

}
