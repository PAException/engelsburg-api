package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateArticleRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.ArticleResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArticleController {

	@Autowired private ArticleRepository articleRepository;

	public void createArticle(CreateArticleRequestDTO dto) {
		this.articleRepository.save(new ArticleModel(
				-1,
				dto.getDate(),
				dto.getLink(),
				dto.getTitle(),
				dto.getContent()
		));
	}

	public Result<GetArticlesResponseDTO> getArticlesAfter(long date, int page, int size) {
		//TODO: replace with hibernate validator in endpoint
		if (date < 0) return Result.of(Error.INVALID_PARAM, "date must be 0 or greater");
		if (page < 1) return Result.of(Error.INVALID_PARAM, "page must be greater than 0");
		if (size < 1 || size > 20) return Result.of(Error.INVALID_PARAM, "size must be between 1 and 20");

		List<ArticleResponseDTO> responseDTOs = new ArrayList<>();
		this.articleRepository.findAllByDateGreaterThanEqual(date, PageRequest.of(page-1, size))
				.forEach(article -> responseDTOs.add(article.toResponseDTO()));
		if (responseDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, "article");
		System.out.println(responseDTOs);

		return Result.of(new GetArticlesResponseDTO(responseDTOs));
	}

	@Transactional
	public void clearAllArticles() {
		this.articleRepository.deleteAll();
	}

}
