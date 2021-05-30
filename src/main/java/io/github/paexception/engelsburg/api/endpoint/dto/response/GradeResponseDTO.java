package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponseDTO {

	private UUID userId;
	private String name;
	private double share;
	private int value;
	private String subject;

}
