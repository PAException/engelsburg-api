package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.ArticleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleEndpoint {

	@Autowired private ArticleController articleController;

	@GetMapping("/article")
	private Object getArticles(@RequestParam(required = false, defaultValue = "0") long date,
							   @RequestParam(required = false, defaultValue = "1") int page,
							   @RequestParam(required = false, defaultValue = "10") int size) {
		return this.articleController.getArticlesAfter(date, page, size).getHttpResponse();
	}

}