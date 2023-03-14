/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

	@Schema(example = "21")
	private int taskId;
	@Schema(example = "Example task")
	private String title;
	@Schema(example = "1645526252719")
	private long created;
	@Schema(example = "1645612362366", description = "0 = not due")
	private long due;
	@Schema(example = "1423")
	private int subjectId;
	@Schema(example = "Example content of a task")
	private String content;
	@Schema(example = "true")
	private boolean done;

}
