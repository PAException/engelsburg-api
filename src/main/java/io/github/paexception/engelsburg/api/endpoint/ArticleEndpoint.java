package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.ArticleController;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for article actions
 */
@RestController
public class ArticleEndpoint {

	@Autowired
	private ArticleController articleController;

	/**
	 * Return article by specific params
	 *
	 * @see ArticleController#getArticlesAfter(long, Paging)
	 */
	@GetMapping("/article")
	private Object getArticles(@RequestParam(required = false, defaultValue = "-1") long date, Paging paging) {
		return this.articleController.getArticlesAfter(date, paging).getHttpResponse();
	}

}
