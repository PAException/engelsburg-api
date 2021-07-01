package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDTO {

	@Range(min = 0, max = 4)
	private int day = -1;
	@Range(min = 0, max = 10)
	private int lesson = -1;
	private String teacher;
	private String className;
	private String room;
	private String subject;

}
