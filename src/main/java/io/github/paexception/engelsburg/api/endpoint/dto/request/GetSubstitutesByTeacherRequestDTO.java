package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubstitutesByTeacherRequestDTO {

	@NotBlank
	private String teacher;
	private int lesson = -1;
	private String className;
	private long date = -1;

}
