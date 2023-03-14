/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeShareRequestDTO {

	@Range(min = 0, max = 1)
	@Schema(example = "0.5")
	private double share;
	@NotBlank
	@Schema(example = "Klausuren")
	private String name;
	@NotBlank
	@Schema(example = "1423")
	private int subjectId;

}
