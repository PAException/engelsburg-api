package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
	@Min(0)
	private double share;
	@Min(0)
	private int value;
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
