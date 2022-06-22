/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.shared;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.projections.ArticleIdAndContentHashProjection;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ArticlesUpdatedRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.ArticlesUpdatedResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.spring.paging.AbstractPageable;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.util.Constants.Article.NAME_KEY;

/**
 * Controller for articles.
 */
@Component
public class ArticleController extends AbstractPageable {

	private final ArticleRepository articleRepository;

	public ArticleController(ArticleRepository articleRepository) {
		//Set paging information
		super(1, 20);
		this.articleRepository = articleRepository;
	}

	/**
	 * Create or update an article.
	 *
	 * @param dto which has article information
	 */
	public void createOrUpdateArticle(ArticleDTO dto) {
		Optional<ArticleModel> optionalArticle = this.articleRepository.findByArticleId(dto.getArticleId());
		if (optionalArticle.isPresent()) {
			this.articleRepository.save(optionalArticle.get().update(dto));
		} else {
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
	 * Get all article ids with hashes to check for changes.
	 *
	 * @return ids and hashes
	 */
	public List<ArticleIdAndContentHashProjection> prepareArticleToUpdate() {
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
	 * @param dto hashes of articles
	 * @return list of articles that were updated
	 */
	public Result<ArticlesUpdatedResponseDTO> checkArticlesUpdated(ArticlesUpdatedRequestDTO dto) {
		List<String> hashes = new ArrayList<>();
		for (String hash : dto.getHashes()) {
			if (!this.articleRepository.existsByContentHash(hash)) hashes.add(hash);
		}

		return Result.of(new ArticlesUpdatedResponseDTO(hashes));
	}

	/**
	 * Get specific article.
	 *
	 * @param articleId id of article
	 * @return article
	 */
	public Result<ArticleDTO> getArticle(int articleId) {
		return this.articleRepository.findByArticleId(articleId).map(article -> Result.of(article.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Check if article exists.
	 *
	 * @param articleId to check
	 * @return empty result of exists, otherwise error
	 */
	public Result<?> exists(int articleId) {
		return this.articleRepository.existsByArticleId(articleId)
				? Result.empty()
				: Result.of(Error.NOT_FOUND, NAME_KEY);
	}
}
