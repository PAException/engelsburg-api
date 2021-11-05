package io.github.paexception.engelsburg.api.controller.shared;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.projections.ArticleIdAndContentHash;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.database.repository.ArticleSaveRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.ArticlesUpdatedResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetAllSavedArticlesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.spring.paging.AbstractPageable;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Article.NAME_KEY;

/**
 * Controller for articles.
 */
@Component
public class ArticleController extends AbstractPageable implements UserDataHandler {

	private final ArticleRepository articleRepository;
	private final ArticleSaveRepository articleSaveRepository;

	/**
	 * Paging information.
	 *
	 * @param articleRepository     injection
	 * @param articleSaveRepository injection
	 */
	public ArticleController(ArticleRepository articleRepository, ArticleSaveRepository articleSaveRepository) {
		super(1, 20);
		this.articleRepository = articleRepository;
		this.articleSaveRepository = articleSaveRepository;
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
					dto.getArticleId(),
					dto.getDate(),
					dto.getLink(),
					dto.getTitle(),
					dto.getContent(),
					dto.getContentHash(),
					dto.getMediaUrl(),
					dto.getBlurHash()
			));
		}
	}

	/**
	 * Update a specific article.
	 *
	 * @param dto article info
	 */
	public void updateArticle(ArticleDTO dto) {
		Optional<ArticleModel> optionalArticle = this.articleRepository.findByArticleId(dto.getArticleId());
		if (optionalArticle.isEmpty()) this.createArticle(dto);
		else this.articleRepository.save(optionalArticle.get().update(dto));
	}

	/**
	 * Get all article ids with hashes to check for changes.
	 *
	 * @return ids and hashes
	 */
	public List<ArticleIdAndContentHash> prepareArticleToUpdate() {
		return this.articleRepository.findAllIdsAndContentHashes();
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
	 * Check if any article was updated.
	 *
	 * @param idsAndHashes of articles
	 * @return list of articles that were updated
	 */
	public Result<ArticlesUpdatedResponseDTO> checkArticlesUpdated(Map<String, String> idsAndHashes) {
		List<Integer> idsToUpdate = new ArrayList<>();

		idsAndHashes.forEach((id, hash) -> {
			Optional<ArticleModel> optionalArticle = this.articleRepository.findByArticleId(Integer.parseInt(id));
			if (optionalArticle.isPresent() && !optionalArticle.get().getContentHash().equals(hash))
				idsToUpdate.add(Integer.parseInt(id));
		});

		return Result.of(new ArticlesUpdatedResponseDTO(idsToUpdate));
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
	 * @param userDTO   user information
	 * @return error or empty result
	 */
	@Transactional
	public Result<?> saveArticle(boolean value, int articleId, UserDTO userDTO) {
		if (!this.articleRepository.existsById(articleId)) return Result.of(Error.NOT_FOUND, NAME_KEY);

		if (value) {
			if (this.articleSaveRepository.existsByArticleIdAndUser(articleId, userDTO.user))
				return Result.of(Error.ALREADY_EXISTS, "article_save");

			this.articleSaveRepository.save(new ArticleSaveModel(-1, userDTO.user, articleId));
		} else {
			if (!this.articleSaveRepository.existsByArticleIdAndUser(articleId, userDTO.user))
				return Result.of(Error.NOT_FOUND, "article_save");

			this.articleSaveRepository.deleteByArticleIdAndUser(articleId, userDTO.user);
		}

		return Result.empty();
	}

	/**
	 * Return all saved articles of user.
	 *
	 * @param userDTO user information
	 * @return saved articles
	 */
	public Result<GetAllSavedArticlesResponseDTO> getSavedArticles(UserDTO userDTO) {
		List<Integer> savedArticles = this.articleSaveRepository
				.findAllByUser(userDTO.user).stream().map(ArticleSaveModel::getArticleId).collect(
						Collectors.toList());

		if (savedArticles.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetAllSavedArticlesResponseDTO(savedArticles));
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.articleSaveRepository.deleteAllByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.articleSaveRepository.findAllByUser(user));
	}

}
