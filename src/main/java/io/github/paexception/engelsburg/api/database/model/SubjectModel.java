/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.endpoint.dto.SubjectDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class SubjectModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int subjectId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "semester_semesterId")
	private SemesterModel semester;

	@NotBlank
	private String baseSubject;
	@NotBlank
	private String customName;
	@NotBlank
	private String color;
	private boolean advancedCourse;


	@Setter(AccessLevel.NONE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subject")
	private List<GradeShareModel> gradeShares;
	@Setter(AccessLevel.NONE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subject")
	private List<TimetableModel> timetable;
	@Setter(AccessLevel.NONE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subject")
	private List<TaskModel> tasks;

	public SubjectModel(int subjectId, SemesterModel semester, String baseSubject, String customName, String color,
			boolean advancedCourse) {
		this.subjectId = subjectId;
		this.semester = semester;
		this.baseSubject = baseSubject;
		this.customName = customName;
		this.color = color;
		this.advancedCourse = advancedCourse;
	}

	public boolean hasDepending() {
		return !this.timetable.isEmpty() || !this.gradeShares.isEmpty();
	}

	@PreRemove
	public void removeNullableAssociations() {
		this.tasks.forEach(task -> task.setSubject(null));
	}

	public SubjectDTO toResponseDTO() {
		return new SubjectDTO(this.subjectId, this.baseSubject, this.customName, this.color, this.advancedCourse);
	}

	public SubjectModel copy() {
		return new SubjectModel(
				this.subjectId,
				this.semester,
				this.baseSubject,
				this.customName,
				this.color,
				this.advancedCourse
		);
	}
}
