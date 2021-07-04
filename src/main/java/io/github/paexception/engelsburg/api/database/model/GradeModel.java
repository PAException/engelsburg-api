package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class GradeModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int gradeId;

	@NotNull
	@Column(length = 16)
	private UUID userId;

	@NotBlank
	private String name;
	private double share;
	@Range(min = 0, max = 15)
	private int value;
	@NotBlank
	private String subject;

	public GradeDTO toResponseDTO() {
		return new GradeDTO(
				this.gradeId,
				this.name,
				this.share,
				this.value,
				this.subject
		);
	}

}
