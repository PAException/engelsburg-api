package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDTO {

	private boolean enabled;
	private boolean byClass;
	private String className;
	private boolean byTeacher;
	private String teacherAbbreviation;
	private boolean byTimetable;
	private boolean articleNotifications;

}
