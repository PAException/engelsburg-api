package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetArticlesResponseDTO {

	@NotNull
	private List<ArticleResponseDTO> articles;

}
