package io.github.paexception.engelsburg.api.controller.shared;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.database.repository.ArticleSaveRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetAllSavedArticlesResponseDTO;
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
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Article.NAME_KEY;

/**
 * Controller for articles.
 */
@Component
public class ArticleController extends AbstractPageable implements UserDataHandler {

	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private ArticleSaveRepository articleSaveRepository;

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
					.forEach(article -> responseDTOs.add(article.toResponseDTO()));
		} else this.articleRepository.findAllByDateGreaterThanEqualOrderByDateAsc(date, this.toPage(paging))
				.forEach(article -> responseDTOs.add(article.toResponseDTO()));
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
		return this.articleRepository.findById(articleId).map(article -> Result.of(article.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
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
	public Result<?> saveArticle(boolean value, int articleId, DecodedJWT jwt) {
		if (!this.articleRepository.existsById(articleId)) return Result.of(Error.NOT_FOUND, NAME_KEY);

		UUID userId = UUID.fromString(jwt.getSubject());
		if (value) {
			if (this.articleSaveRepository.existsByArticleIdAndUserId(articleId, userId))
				return Result.of(Error.ALREADY_EXISTS, "article_like");

			this.articleSaveRepository.save(new ArticleSaveModel(-1, userId, articleId));
		} else {
			if (!this.articleSaveRepository.existsByArticleIdAndUserId(articleId, userId))
				return Result.of(Error.NOT_FOUND, "article_like");

			this.articleSaveRepository.deleteByArticleIdAndUserId(articleId, userId);
		}

		return Result.empty();
	}

	/**
	 * Return all saved articles of user.
	 *
	 * @param jwt to get userId
	 * @return saved articles
	 */
	public Result<GetAllSavedArticlesResponseDTO> getSavedArticles(DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());

		List<Integer> savedArticles = this.articleSaveRepository
				.findAllByUserId(userId).stream().map(ArticleSaveModel::getArticleId).collect(Collectors.toList());

		if (savedArticles.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetAllSavedArticlesResponseDTO(savedArticles));
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.articleSaveRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.articleSaveRepository.findAllByUserId(userId));
	}

}
