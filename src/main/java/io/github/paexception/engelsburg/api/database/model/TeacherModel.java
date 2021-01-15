package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.response.TeacherResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TeacherModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int teacherId;
	@NotBlank
	private String abbreviation;
	@NotBlank
	private String firstname;
	@NotBlank
	private String surname;
	@NotBlank
	private String gender;
	private boolean mentionedPhD;
	private long job;

	public TeacherResponseDTO toResponseDTO() {
		return new TeacherResponseDTO(
				this.abbreviation,
				this.firstname,
				this.surname,
				this.gender,
				this.mentionedPhD,
				this.job
		);
	}

}
