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
public class UpdateTaskRequestDTO {

	@Min(1)
	@Schema(example = "21")
	private int taskId = -1;
	@Schema(example = "Updated title")
	private String title;
	@Schema(example = "1645612362366", description = "0 = not due")
	private long due = -1; //0 => not due
	@Min(1)
	@Schema(example = "1423")
	private int subjectId = -1;
	@Schema(example = "Example of content that was updated")
	private String content;

}
