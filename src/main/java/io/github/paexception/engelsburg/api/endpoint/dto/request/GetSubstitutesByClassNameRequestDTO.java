package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubstitutesByClassNameRequestDTO {

	@NotBlank
	@Length(min = 2, max = 3)
	private String className;
	private long date = -1;

}
