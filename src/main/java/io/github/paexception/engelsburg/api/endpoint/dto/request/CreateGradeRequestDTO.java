package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeRequestDTO {

	@NotBlank
	private String name;
	private double share;
	@Range(min = 0, max = 15)
	private int value = -1;
	@NotBlank
	private String subject;

}
