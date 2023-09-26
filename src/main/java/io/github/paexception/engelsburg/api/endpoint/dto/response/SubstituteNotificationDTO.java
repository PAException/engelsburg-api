/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.response;

import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteNotificationDTO {

	private Date date;
	private String className;
	private String lesson;
	private String subject;
	private String substituteTeacher;
	private String teacher;
	private String type;
	private String substituteOf;
	private String room;
	private String text;

	public static SubstituteNotificationDTO fromSubstituteDTO(SubstituteDTO dto, String lesson) {
		return new SubstituteNotificationDTO(
				dto.getDate(),
				dto.getClassName(),
				lesson == null ? String.valueOf(dto.getLesson()) : lesson,
				dto.getSubject(),
				dto.getSubstituteTeacher(),
				dto.getTeacher(),
				dto.getType(),
				dto.getSubstituteOf(),
				dto.getRoom(),
				dto.getText()
		);
	}

}
