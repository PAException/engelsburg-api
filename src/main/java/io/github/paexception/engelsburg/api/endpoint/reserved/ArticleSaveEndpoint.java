/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.ArticleSaveController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSavedArticlesResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Min;

/**
 * RestController for article saves.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/save")
public class ArticleSaveEndpoint {

	private final ArticleSaveController articleSaveController;

	/**
	 * Save specific article.
	 *
	 * @see ArticleSaveController#saveArticle(boolean, int, UserDTO)
	 */
	@AuthScope("article.save.write.self")
	@PatchMapping("/{articleId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article")
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "article_save")
	public Object saveArticle(@PathVariable @Min(1) @Schema(example = "108") int articleId, UserDTO userDTO) {
		return this.articleSaveController.saveArticle(true, articleId, userDTO).getHttpResponse();
	}

	/**
	 * Remove save of specific article.
	 *
	 * @see ArticleSaveController#saveArticle(boolean, int, UserDTO)
	 */
	@AuthScope("article.save.delete.self")
	@DeleteMapping("/{articleId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article", key = "Article")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article_save", key = "Article save")
	public Object unsaveArticle(@PathVariable @Min(1) @Schema(example = "108") int articleId, UserDTO userDTO) {
		return this.articleSaveController.saveArticle(false, articleId, userDTO).getHttpResponse();
	}

	/**
	 * Get saved articles of user.
	 *
	 * @see ArticleSaveController#getSavedArticles(UserDTO)
	 */
	@AuthScope("article.save.read.self")
	@GetMapping
	@Response(GetSavedArticlesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "article_save")
	public Object getSavedArticles(UserDTO userDTO) {
		return this.articleSaveController.getSavedArticles(userDTO).getHttpResponse();
	}

}
