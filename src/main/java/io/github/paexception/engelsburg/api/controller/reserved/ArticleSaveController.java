/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleSaveRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSavedArticlesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.ArticleSave.NAME_KEY;

/**
 * Class to handle saves of articles.
 */
@Component
@AllArgsConstructor
public class ArticleSaveController {

	private final ArticleSaveRepository articleSaveRepository;
	private final ArticleController articleController;

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
		//Check if article exists
		Result<?> result = this.articleController.exists(articleId);
		if (result.isErrorPresent()) return result;

		if (value) { //Add article save
			//Check if article save already exists
			if (this.articleSaveRepository.existsByArticleIdAndUser(articleId, userDTO.user))
				return Result.of(Error.ALREADY_EXISTS, NAME_KEY);

			//Create new article save
			this.articleSaveRepository.save(new ArticleSaveModel(-1, userDTO.user, articleId));
		} else { //Remove article save
			//Check if article save exists
			if (!this.articleSaveRepository.existsByArticleIdAndUser(articleId, userDTO.user))
				return Result.of(Error.NOT_FOUND, NAME_KEY);

			//Remove article save
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
	public Result<GetSavedArticlesResponseDTO> getSavedArticles(UserDTO userDTO) {
		//Find all saved articles by user, get articleIds of saved articles
		List<Integer> savedArticles = this.articleSaveRepository
				.findAllByUser(userDTO.user).stream()
				.map(ArticleSaveModel::getArticleId)
				.collect(Collectors.toList());

		//If none found return error, otherwise dto with articleIds of saved
		if (savedArticles.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetSavedArticlesResponseDTO(savedArticles));
	}
}
