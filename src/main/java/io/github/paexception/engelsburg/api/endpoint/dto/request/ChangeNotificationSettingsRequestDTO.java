package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeNotificationSettingsRequestDTO {

	private boolean enabled;
	private boolean byClass;
	private String className;
	private boolean byTeacher;
	private String teacherAbbreviation;
	private boolean byTimetable;

}
