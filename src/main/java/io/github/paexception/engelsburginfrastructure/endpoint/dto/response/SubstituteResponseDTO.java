package io.github.paexception.engelsburginfrastructure.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstituteResponseDTO {

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

}
