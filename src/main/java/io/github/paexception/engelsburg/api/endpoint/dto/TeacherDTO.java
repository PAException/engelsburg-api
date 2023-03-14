/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {

	@Schema(example = "JÜN")
	private String abbreviation;
	@Schema(example = "Eileen")
	private String firstname;
	@Schema(example = "Jünemann")
	private String surname;
	@Schema(example = "female")
	private String gender;
	@Schema(example = "false")
	private boolean mentionedPhD;
	@Schema(example = "[\"Englisch\",\"Latein\"]")
	private List<String> jobs;

}
