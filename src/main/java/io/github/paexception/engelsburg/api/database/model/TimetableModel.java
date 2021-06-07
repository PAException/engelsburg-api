package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
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
public class TimetableModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int timetableId;

	@NotNull
	@Column(length = 16)
	private UUID userId;
	@Min(0)
	private int day;
	@Min(1)
	private int lesson;
	private String teacher;
	private String className;
	private String room;
	@NotBlank
	private String subject;

	public TimetableDTO toResponseDTO() {
		return new TimetableDTO(
				this.day,
				this.lesson,
				this.teacher,
				this.className,
				this.room,
				this.subject
		);
	}

}
