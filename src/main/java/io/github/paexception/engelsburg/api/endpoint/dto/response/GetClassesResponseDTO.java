package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetClassesResponseDTO {

	private String[] classes;

}
