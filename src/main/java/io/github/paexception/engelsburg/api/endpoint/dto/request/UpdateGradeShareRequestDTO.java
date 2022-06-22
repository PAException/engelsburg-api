/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGradeShareRequestDTO {

	@Min(1)
	@Schema(example = "14")
	private int gradeShareId;
	@Schema(example = "0.33")
	private double share = -1;
	@Schema(example = "Mündlich")
	private String name;

}
