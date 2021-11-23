package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TaskModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int taskId;

	@ManyToOne
	private UserModel user;

	@NotBlank
	private String title;
	@Min(0)
	private long created;
	@Min(-1)
	private long due;
	private String subject;
	@Lob
	private String content;
	private boolean done;

	public TaskDTO toResponseDTO() {
		return new TaskDTO(
				this.taskId,
				this.title,
				this.created,
				this.due,
				this.subject,
				this.content,
				this.done
		);
	}

	public TaskModel markAsDone(boolean done) {
		this.done = done;

		return this;
	}

}
