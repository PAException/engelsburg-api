/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubstituteMessageRequestDTO {

	@NotNull
	private Date date;
	private String absenceTeachers;
	private String absenceClasses;
	private String affectedClasses;
	private String affectedRooms;
	private String blockedRooms;
	private String messages;

}
