package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGradeRequestDTO {

	private int gradeId = -1;
	private String name;
	private double share = -1;
	private int value = -1;
	private String subject;

}
