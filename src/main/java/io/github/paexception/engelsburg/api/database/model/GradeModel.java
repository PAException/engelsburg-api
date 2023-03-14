/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class GradeModel {

	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int gradeId;

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "gradeShare_gradeShareId")
	private GradeShareModel gradeShare;

	@NotBlank
	private String name;
	@Range(min = 0, max = 15)
	private int value;

	public GradeDTO toResponseDTO() {
		return new GradeDTO(
				this.gradeId,
				this.name,
				this.value,
				this.gradeShare.getGradeShareId()
		);
	}
}
