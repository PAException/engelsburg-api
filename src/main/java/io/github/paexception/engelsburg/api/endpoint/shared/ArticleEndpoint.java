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
	 * Like specific article.
	 *
	 * @see ArticleController#likeArticle(boolean, int, DecodedJWT)
	 */
	@AuthScope("article.like.write.self")
	@PatchMapping("/article/like/{articleId}")
	public Object likeArticle(@PathVariable int articleId, DecodedJWT jwt) {
		return this.articleController.likeArticle(true, articleId, jwt).getHttpResponse();
	}

	/**
	 * Remove like of specific article.
	 *
	 * @see ArticleController#likeArticle(boolean, int, DecodedJWT)
	 */
	@AuthScope("article.like.delete.self")
	@DeleteMapping("/article/like/{articleId}")
	public Object unlikeArticle(@PathVariable int articleId, DecodedJWT jwt) {
		return this.articleController.likeArticle(false, articleId, jwt).getHttpResponse();
	}

}
