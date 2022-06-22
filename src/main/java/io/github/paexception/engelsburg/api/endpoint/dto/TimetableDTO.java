/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDTO {

	@Range(min = 0, max = 4)
	@Schema(example = "2")
	private int day = -1;
	@Range(min = 0, max = 12)
	@Schema(example = "5")
	private int lesson = -1;
	@Schema(example = "1423", required = true)
	private int subjectId;
	@Schema(example = "LAN")
	private String teacher;
	@Schema(example = "10c")
	private String className;
	@Schema(example = "A301")
	private String room;

}
