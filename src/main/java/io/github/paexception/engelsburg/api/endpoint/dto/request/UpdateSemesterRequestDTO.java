/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSemesterRequestDTO {

	@Min(1)
	@Schema(example = "43")
	private int semesterId;
	@Schema(example = "2020")
	private int schoolYear = -1;
	@Schema(example = "21")
	private int semester = -1;
	@Schema(example = "a")
	@Nullable
	private String classSuffix;
}
