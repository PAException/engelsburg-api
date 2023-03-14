/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeShareDTO {

	@Schema(example = "14")
	private int gradeShareId;
	@Schema(example = "0.5")
	private double share;
	@Schema(example = "Klausuren")
	private String name;
	@Schema(example = "1423")
	private int subjectId;

}
