/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteMessageDTO {

	@Schema(example = "2022-02-21", format = "date", type = "string")
	private Date date;
	@Schema(example = "ANS, GLE, IHM, KOP, KÜH, LIN, MÖM, MÜM, SPR")
	private String absenceTeachers;
	@Schema(example = "7c, E2")
	private String absenceClasses;
	@Schema(example = "5a, 5b, 5c, 5d, 6a, 6b, 6c, 6d, 6e, 7d, 8c, 8d, 9a, 9b, 9d, 10a, 10c, Q2, Q4")
	private String affectedClasses;
	@Schema(example = "H 001, H 101, H 102, H 103, H 106, H 109, H 201, H 202, H 203, H 205, H 207")
	private String affectedRooms;
	@Schema(example = "Aula (1-6)")
	private String blockedRooms;
	@Schema(example = "Nachmittagsunterricht entfällt!")
	private String messages;

}
