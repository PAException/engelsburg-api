/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

	@Schema(example = "1423")
	private int subjectId;
	@Schema(example = "math")
	private String baseSubject;
	@Schema(example = "Mathe")
	private String customName;
	@Schema(example = "#2040f7")
	private String color;
	@Schema(example = "true")
	private boolean advancedCourse;

}
