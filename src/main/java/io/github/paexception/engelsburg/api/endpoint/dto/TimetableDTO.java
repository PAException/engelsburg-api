package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDTO {

	private int day;
	private int lesson;
	private String teacher;
	private String className;
	private String room;
	private String subject;

}
