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
public class GradeDTO {

	@Schema(example = "11")
	private int gradeId;
	@Schema(example = "1. Klausur")
	private String name;
	@Schema(example = "12")
	private int value;
	@Schema(example = "14")
	private int gradeShareId;

}
