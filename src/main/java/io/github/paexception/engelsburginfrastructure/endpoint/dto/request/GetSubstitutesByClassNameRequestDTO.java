package io.github.paexception.engelsburginfrastructure.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubstitutesByClassNameRequestDTO {

	@NotBlank
	private String className;
	private long date;

}
