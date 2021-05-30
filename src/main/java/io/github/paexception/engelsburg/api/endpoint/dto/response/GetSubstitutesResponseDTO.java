package io.github.paexception.engelsburg.api.endpoint.dto.response;

import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubstitutesResponseDTO {

	@NotNull
	private List<SubstituteDTO> substitutes;

}
