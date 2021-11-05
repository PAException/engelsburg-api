package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * RestController for article actions.
 */
@RestController
public class ArticleEndpoint {

	private final ArticleController articleController;

	public ArticleEndpoint(ArticleController articleController) {
		this.articleController = articleController;
	}

	/**
	 * Return article by specific params.
	 *
	 * @see ArticleController#getArticlesAfter(long, Paging)
	 */
	@GetMapping("/article")
	public Object getArticles(@RequestParam(required = false, defaultValue = "-1") long date, Paging paging) {
		return this.articleController.getArticlesAfter(date, paging).getHttpResponse();
	}

	/**
	 * Return specific article.
	 *
	 * @see ArticleController#getArticle(int)
	 */
	@GetMapping("/article/{articleId}")
	public Object getArticle(@PathVariable int articleId) {
		return this.articleController.getArticle(articleId).getHttpResponse();
	}

	/**
	 * Check if article were updated.
	 *
	 * @see ArticleController#checkArticlesUpdated(Map)
	 */
	@PatchMapping("/article/updated")
	public Object checkArticlesUpdated(@RequestBody Map<String, String> idsAndHashes) {
		return this.articleController.checkArticlesUpdated(idsAndHashes).getHttpResponse();
	}

	/**
	 * Save specific article.
	 *
	 * @see ArticleController#saveArticle(boolean, int, UserDTO)
	 */
	@AuthScope("article.save.write.self")
	@PatchMapping("/article/save/{articleId}")
	public Object saveArticle(@PathVariable int articleId, UserDTO userDTO) {
		return this.articleController.saveArticle(true, articleId, userDTO).getHttpResponse();
	}

	/**
	 * Remove save of specific article.
	 *
	 * @see ArticleController#saveArticle(boolean, int, UserDTO)
	 */
	@AuthScope("article.save.delete.self")
	@DeleteMapping("/article/save/{articleId}")
	public Object unsaveArticle(@PathVariable int articleId, UserDTO userDTO) {
		return this.articleController.saveArticle(false, articleId, userDTO).getHttpResponse();
	}

	/**
	 * Get saved articles of user.
	 *
	 * @see ArticleController#getSavedArticles(UserDTO)
	 */
	@AuthScope("article.save.read.self")
	@GetMapping("/article/save")
	public Object getSavedArticles(UserDTO userDTO) {
		return this.articleController.getSavedArticles(userDTO).getHttpResponse();
	}


}
