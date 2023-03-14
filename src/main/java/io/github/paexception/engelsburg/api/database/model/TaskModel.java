/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.paexception.engelsburg.api.endpoint.dto.TaskDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TaskModel {

	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int taskId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "semester_semesterId")
	private SemesterModel semester;
	@Nullable
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subjectId")
	@ManyToOne
	@JoinColumn(columnDefinition = "integer", name = "subject_subjectId")
	private SubjectModel subject;

	@NotBlank
	private String title;
	@Min(0)
	private long created;
	@Min(0)
	private long due; //0 => not due
	@Lob
	private String content;
	private boolean done;

	public TaskDTO toResponseDTO() {
		return new TaskDTO(
				this.taskId,
				this.title,
				this.created,
				this.due,
				this.subject != null ? this.subject.getSubjectId() : -1,
				this.content,
				this.done
		);
	}

	public TaskModel markAsDone(boolean done) {
		this.done = done;

		return this;
	}
}
