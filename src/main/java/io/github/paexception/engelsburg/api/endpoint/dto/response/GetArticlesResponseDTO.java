package io.github.paexception.engelsburg.api.endpoint.dto.response;

import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetArticlesResponseDTO {

	private List<ArticleDTO> articles;

}
