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
public class CreateTaskRequestDTO {

	@NotBlank
	@Schema(example = "Example task")
	private String title;
	@Schema(example = "1645526252719", description = "Not present = current Timestamp")
	private long created = -1;
	@Schema(example = "1645612362366", description = "Not present = 0 = not due")
	private long due = -1;
	@Schema(example = "1423")
	private int subjectId;
	@Schema(example = "Example content of a task")
	private String content;

}
