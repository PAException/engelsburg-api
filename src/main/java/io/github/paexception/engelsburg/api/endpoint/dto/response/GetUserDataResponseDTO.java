package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDataResponseDTO {

	@NotNull
	private UUID userId;

	private List<GetUserDataResponseDTOModel> data;

}
