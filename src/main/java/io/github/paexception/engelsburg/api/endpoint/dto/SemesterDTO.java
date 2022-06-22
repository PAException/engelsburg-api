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
public class SemesterDTO {

	@Schema(example = "43")
	private int semesterId;
	@Schema(example = "2021")
	private int schoolYear;
	@Schema(example = "25")
	private int semester; //class * 2 (+ 1 if summer semester)
	@Schema(example = "c")
	private String classSuffix;
}
