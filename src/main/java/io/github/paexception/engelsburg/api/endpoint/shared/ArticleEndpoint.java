/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ArticlesUpdatedRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.ArticlesUpdatedResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * RestController for article actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article")
public class ArticleEndpoint {

	private final ArticleController articleController;

	/**
	 * Return article by specific params.
	 *
	 * @see ArticleController#getArticlesAfter(long, Paging)
	 */
	@GetMapping
	@Response(GetArticlesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article")
	public Object getArticles(@RequestParam(required = false, defaultValue = "-1") long date, Paging paging) {
		return this.articleController.getArticlesAfter(date, paging).getHttpResponse();
	}

	/**
	 * Return specific article.
	 *
	 * @see ArticleController#getArticle(int)
	 */
	@GetMapping("/{articleId}")
	@Response(ArticleDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article")
	public Object getArticle(@PathVariable @Min(0) int articleId) {
		return this.articleController.getArticle(articleId).getHttpResponse();
	}

	/**
	 * Check if article were updated.
	 *
	 * @see ArticleController#checkArticlesUpdated(ArticlesUpdatedRequestDTO)
	 */
	@PatchMapping
	@Response(ArticlesUpdatedResponseDTO.class)
	public Object checkArticlesUpdated(@RequestBody @Valid ArticlesUpdatedRequestDTO dto) {
		return this.articleController.checkArticlesUpdated(dto).getHttpResponse();
	}
}
