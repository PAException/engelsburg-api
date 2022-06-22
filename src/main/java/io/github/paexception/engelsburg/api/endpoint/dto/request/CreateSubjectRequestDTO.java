/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectRequestDTO {

	@NotBlank
	@Schema(example = "math")
	private String baseSubject;
	@Schema(example = "Mathe")
	private String customName;
	@NotBlank
	@Schema(example = "#2040f7")
	private String color;
	@Schema(example = "true", defaultValue = "false")
	private boolean advancedCourse = false;

}
