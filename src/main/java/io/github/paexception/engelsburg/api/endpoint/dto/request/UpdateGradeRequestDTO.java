/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGradeRequestDTO {

	@Min(1)
	@Schema(example = "11")
	private int gradeId;
	@Schema(example = "2. Klausur")
	private String name;
	@Schema(example = "13")
	private int value = -1;
	@Schema(example = "14")
	private int gradeShareId;

}
