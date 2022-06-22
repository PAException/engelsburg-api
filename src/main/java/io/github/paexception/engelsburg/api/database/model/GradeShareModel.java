/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeShareDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class GradeShareModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int gradeShareId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "subject_subjectId")
	private SubjectModel subject;

	@Range(min = 0, max = 1)
	private double share;
	@NotBlank
	private String name;


	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gradeShare")
	private List<GradeModel> grades;

	public GradeShareModel(int gradeShareId, SubjectModel subject, double share, String name) {
		this.gradeShareId = gradeShareId;
		this.subject = subject;
		this.share = share;
		this.name = name;
	}

	public boolean hasDepending() {
		return !this.grades.isEmpty();
	}

	public GradeShareDTO toResponseDTO() {
		return new GradeShareDTO(
				this.gradeShareId,
				this.share,
				this.name,
				this.subject.getSubjectId()
		);
	}
}
