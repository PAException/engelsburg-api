package io.github.paexception.engelsburg.api.controller.shared;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.database.model.ArticleLikeModel;
import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleLikeRepository;
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
import java.util.UUID;
import static io.github.paexception.engelsburg.api.util.Constants.Article.NAME_KEY;

/**
 * Controller for articles.
 */
@Component
public class ArticleController extends AbstractPageable {

	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private ArticleLikeRepository articleLikeRepository;

	/**
	 * Paging information.
	 */
	public ArticleController() {
		super(1, 20);
	}

	/**
	 * Create a new Article.
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
					dto.getMediaUrl(),
					dto.getBlurHash()
			));
		}
	}

	/**
	 * Get articles after date with pagination.
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
					.forEach(article -> responseDTOs.add(article.toResponseDTO(this.getLikes(article))));
		} else this.articleRepository.findAllByDateGreaterThanEqualOrderByDateAsc(date, this.toPage(paging))
				.forEach(article -> responseDTOs.add(article.toResponseDTO(this.getLikes(article))));
		if (responseDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return Result.of(new GetArticlesResponseDTO(responseDTOs));
	}

	/**
	 * Delete all articles.
	 * Only {@link ArticleUpdateService} is supposed to call
	 * this function!
	 */
	@Transactional
	public void clearAllArticles() {
		this.articleRepository.deleteAll();
	}

	/**
	 * Get specific article.
	 *
	 * @param articleId id of article
	 * @return article
	 */
	public Result<ArticleDTO> getArticle(int articleId) {
		return this.articleRepository.findById(articleId).map(article -> Result.of(article.toResponseDTO(this.getLikes(article))))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Private function to get likes of article.
	 *
	 * @param article to get articleId
	 * @return likes
	 */
	private int getLikes(ArticleModel article) {
		return this.articleLikeRepository.countAllByArticleId(article.getArticleId());
	}

	/**
	 * Add or remove like of article.
	 *
	 * @param value     add or remove
	 * @param articleId of article
	 * @param jwt       with userId
	 * @return error or empty result
	 */
	@Transactional
	public Result<?> likeArticle(boolean value, int articleId, DecodedJWT jwt) {
		if (!this.articleRepository.existsById(articleId)) return Result.of(Error.NOT_FOUND, NAME_KEY);

		UUID userId = UUID.fromString(jwt.getSubject());
		if (value) {
			if (this.articleLikeRepository.existsByArticleIdAndUserId(articleId, userId))
				return Result.of(Error.ALREADY_EXISTS, "article_like");

			this.articleLikeRepository.save(new ArticleLikeModel(-1, userId, articleId));
		} else {
			if (!this.articleLikeRepository.existsByArticleIdAndUserId(articleId, userId))
				return Result.of(Error.NOT_FOUND, "article_like");

			this.articleLikeRepository.deleteByArticleIdAndUserId(articleId, userId);
		}

		return Result.empty();
	}

}
