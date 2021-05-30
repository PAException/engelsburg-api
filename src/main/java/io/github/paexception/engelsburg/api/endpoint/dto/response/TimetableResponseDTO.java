package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableResponseDTO {

	private UUID userId;
	private int lesson;
	private String teacher;
	private String className;
	private String room;
	private String subject;

}
