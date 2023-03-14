/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSemesterRequestDTO {

	@Schema(example = "25")
	@Min(1)
	private int semester;
	@Schema(example = "2021", defaultValue = "(Current schoolYear)")
	private int schoolYear = -1;
	@Schema(example = "c")
	@Nullable
	private String classSuffix;

	@Schema(example = "true", defaultValue = "true")
	private boolean setAsCurrentSemester = true;

	@Schema(example = "true", defaultValue = "true")
	private boolean copySubjects = true;
	@Schema(example = "false", defaultValue = "false")
	private boolean copyTimetable = false;
	@Schema(example = "true", defaultValue = "true")
	private boolean copyGradeShares = true;
}
