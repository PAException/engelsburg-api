package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteDTO {

	private Date date;
	private String className;
	private int lesson;
	private String subject;
	private String substituteTeacher;
	private String teacher;
	private String type;
	private String substituteOf;
	private String room;
	private String text;

	public SubstituteDTO appendText(String text) {
		this.text = this.text + " " + text;

		return this;
	}

}
