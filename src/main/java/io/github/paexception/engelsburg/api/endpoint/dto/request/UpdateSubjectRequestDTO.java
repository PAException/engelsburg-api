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
public class UpdateSubjectRequestDTO {

	@Min(1)
	@Schema(example = "1423")
	int subjectId;
	@Schema(example = "Math")
	String customName;
	@Schema(example = "#001799")
	String color;
	@Schema(example = "false", defaultValue = "false")
	boolean advancedCourse = false;

}
