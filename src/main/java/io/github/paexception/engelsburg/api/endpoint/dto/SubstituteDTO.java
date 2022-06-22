/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteDTO {

	@Schema(example = "2022-02-21", format = "date", type = "string")
	private Date date;
	@Schema(example = "10c")
	private String className;
	@Schema(example = "5")
	private int lesson;
	@Schema(example = "M")
	private String subject;
	@Schema(example = "BSU")
	private String substituteTeacher;
	@Schema(example = "GAR")
	private String teacher;
	@Schema(example = "Vertretung")
	private String type;
	@Schema(example = "Mo-21.2. / 4")
	private String substituteOf;
	@Schema(example = "H301")
	private String room;
	@Schema(example = "Aufg. vorhanden")
	private String text;

	public SubstituteDTO appendText(String text) {
		this.text = this.text + " " + text;

		return this;
	}

}
