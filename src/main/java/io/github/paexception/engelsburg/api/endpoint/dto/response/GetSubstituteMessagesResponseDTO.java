package io.github.paexception.engelsburg.api.endpoint.dto.response;

import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubstituteMessagesResponseDTO {

	private List<SubstituteMessageDTO> substituteMessages;

}
