package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteMessageDTO {

	private Date date;
	private String absenceTeachers;
	private String absenceClasses;
	private String affectedClasses;
	private String affectedRooms;
	private String blockedRooms;
	private String messages;

}
