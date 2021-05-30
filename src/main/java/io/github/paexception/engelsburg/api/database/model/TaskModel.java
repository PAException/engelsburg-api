package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.response.TaskResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
public class TaskModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int taskId;

	@NotNull
	@Column(length = 16)
	private UUID userId;

	@NotBlank
	private String title;
	@Min(0)
	private long created;
	@Min(0)
	private long due;
	private String subject;
	@Lob
	private String content;

	public TaskResponseDTO toResponseDTO() {
		return new TaskResponseDTO(
				this.userId,
				this.title,
				this.created,
				this.due,
				this.subject,
				this.content
		);
	}

}
