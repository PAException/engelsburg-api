package io.github.paexception.engelsburg.api.endpoint.shared;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for article actions.
 */
@RestController
public class ArticleEndpoint {

	@Autowired
	private ArticleController articleController;

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
	 * Save specific article.
	 *
	 * @see ArticleController#saveArticle(boolean, int, DecodedJWT)
	 */
	@AuthScope("article.save.write.self")
	@PatchMapping("/article/save/{articleId}")
	public Object likeArticle(@PathVariable int articleId, DecodedJWT jwt) {
		return this.articleController.saveArticle(true, articleId, jwt).getHttpResponse();
	}

	/**
	 * Remove save of specific article.
	 *
	 * @see ArticleController#saveArticle(boolean, int, DecodedJWT)
	 */
	@AuthScope("article.save.delete.self")
	@DeleteMapping("/article/save/{articleId}")
	public Object unsaveArticle(@PathVariable int articleId, DecodedJWT jwt) {
		return this.articleController.saveArticle(false, articleId, jwt).getHttpResponse();
	}

	/**
	 * Get saved articles of user.
	 *
	 * @see ArticleController#getSavedArticles(DecodedJWT)
	 */
	@AuthScope("article.save.read.self")
	@GetMapping("/article/save")
	public Object getSavedArticles(DecodedJWT jwt) {
		return this.articleController.getSavedArticles(jwt).getHttpResponse();
	}


}
