/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.endpoint.dto.SemesterDTO;
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
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class SemesterModel {

	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int semesterId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_userId")
	private UserModel user;

	@Min(1)
	private int schoolYear; // e.g. 2021
	@Min(1)
	private int semester; //class * 2 (+ 1 if summer semester)
	private String classSuffix;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "semester")
	private List<SubjectModel> subjects;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "semester")
	private List<TaskModel> tasks;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "semester")
	private List<TimetableModel> timetable;

	public SemesterModel(UserModel user, int schoolYear, int semester, String classSuffix) {
		this.user = user;
		this.schoolYear = schoolYear;
		this.semester = semester;
		this.classSuffix = classSuffix;
	}

	public boolean hasDepending() {
		return !this.subjects.isEmpty() || !this.tasks.isEmpty() || !this.timetable.isEmpty();
	}

	public SemesterDTO toResponseDTO() {
		return new SemesterDTO(
				this.semesterId,
				this.schoolYear,
				this.semester,
				this.classSuffix
		);
	}
}
