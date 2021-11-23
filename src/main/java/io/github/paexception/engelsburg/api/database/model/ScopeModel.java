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
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ScopeModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scopeId;

	@ManyToOne
	private UserModel user;

	@NotBlank
	private String scope;

}
