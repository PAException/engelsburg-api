package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.service.scheduled.ArticleUpdateService;
import io.github.paexception.engelsburg.api.spring.paging.AbstractPageable;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static io.github.paexception.engelsburg.api.util.Constants.Article.NAME_KEY;

/**
 * Controller for articles
 */
@Component
public class ArticleController extends AbstractPageable {

	@Autowired
	private ArticleRepository articleRepository;

	/**
	 * Paging information
	 */
	public ArticleController() {
		super(1, 20);
	}

	/**
	 * Create a new Article
	 *
	 * @param dto which has article information
	 */
	public void createArticle(ArticleDTO dto) {
		if (!this.articleRepository.existsByDate(dto.getDate())) {
			this.articleRepository.save(new ArticleModel(
					-1,
					dto.getDate(),
					dto.getLink(),
					dto.getTitle(),
					dto.getContent(),
					dto.getMediaUrl()
			));
		}
	}

	/**
	 * Get articles after date with pagination
	 *
	 * @param date   since when articles should be listed
	 * @param paging of articles
	 * @return found articles
	 */
	public Result<GetArticlesResponseDTO> getArticlesAfter(long date, Paging paging) {
		List<ArticleDTO> responseDTOs = new ArrayList<>();
		if (date < 0) {
			date = System.currentTimeMillis();
			this.articleRepository.findAllByDateLessThanEqualOrderByDateDesc(date, this.toPage(paging))
					.forEach(article -> responseDTOs.add(article.toResponseDTO()));
		} else this.articleRepository.findAllByDateGreaterThanEqualOrderByDateAsc(date, this.toPage(paging))
				.forEach(article -> responseDTOs.add(article.toResponseDTO()));
		if (responseDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return Result.of(new GetArticlesResponseDTO(responseDTOs));
	}

	/**
	 * Delete all articles
	 * Only {@link ArticleUpdateService} is supposed to call
	 * this function!
	 */
	@Transactional
	public void clearAllArticles() {
		this.articleRepository.deleteAll();
	}

}
