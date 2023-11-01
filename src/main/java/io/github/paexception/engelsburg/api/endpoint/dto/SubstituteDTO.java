/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.util.Objects;

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

	public boolean sameBase(SubstituteDTO dto) {
		if (!Objects.equals(date, dto.date)) return false;
		if (lesson != dto.lesson) return false;
		if (!Objects.equals(className, dto.className)) return false;

		if (!Character.isDigit(className.charAt(0))) { //Only for E1 - Q4
			return Objects.equals(teacher, dto.teacher);
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubstituteDTO that = (SubstituteDTO) o;
		return lesson == that.lesson && Objects.equals(date, that.date) && Objects.equals(className, that.className) && Objects.equals(subject, that.subject) && Objects.equals(substituteTeacher, that.substituteTeacher) && Objects.equals(teacher, that.teacher) && Objects.equals(type, that.type) && Objects.equals(substituteOf, that.substituteOf) && Objects.equals(room, that.room) && Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, className, lesson, subject, substituteTeacher, teacher, type, substituteOf, room, text);
	}
}
