/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeRequestDTO {

	@NotBlank
	@Schema(example = "1. Klausur")
	private String name;
	@Range(min = 0, max = 15)
	@Schema(example = "12")
	private int value = -1;
	@Schema(example = "14")
	private int gradeShareId;

}
