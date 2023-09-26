/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.database.projections.ArticleIdAndContentHashProjection;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.service.JsonFetchingService;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.WordPressAPI;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service to update articles.
 */
@Service
@AllArgsConstructor
public class ArticleUpdateService extends JsonFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleUpdateService.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static int counter = 0;
	private final ArticleController articleController;
	private final NotificationService notificationService;

	/**
	 * Call {@link #updateArticles(String, int)} every minute and return all articles published in that passed 1 minute.
	 */
	@Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
	public void fetchNewArticles() {
		if (counter != 0) return; //Locked by past fetch

		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[ARTICLE] Fetching...");
		List<ArticleDTO> articles = this.updateArticles(DATE_FORMAT.format(System.currentTimeMillis() - 60 * 1000), 1);
		if (articles.isEmpty()) LOGGER.debug("[ARTICLE] Not updated");
		else LOGGER.debug("[ARTICLE] Fetched " + articles.size());

		for (ArticleDTO article : articles) {
			this.notificationService.sendArticleNotifications(article);
			this.articleController.createOrUpdateArticle(article);
		}
	}

	/**
	 * Checks for updates of articles every 30 minutes.
	 */
	@Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 30 * 60 * 1000)
	public void checkIfArticlesChanged() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[ARTICLE] Starting to check for changes...");
		int counter = 0;
		for (ArticleIdAndContentHashProjection idAndHash : this.articleController.prepareArticleToUpdate()) {
			try {
				JsonElement json = this.request(
						"https://engelsburg.smmp.de/wp-json/wp/v2/posts/" + idAndHash.getArticleId());
				String content = json.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString();
				if (!idAndHash.getContentHash().equals(Result.hash(content))) {
					counter++;
					this.articleController.createOrUpdateArticle(this.createArticleDTO(idAndHash.getArticleId(), json));
				}
			} catch (Exception ignored) {
			}
		}
		if (counter == 0) LOGGER.debug("[ARTICLE] Not changed");
		else LOGGER.info("[ARTICLE] Updated " + counter);
	}

	/**
	 * Fetch all articles past a specific date/time.
	 *
	 * @param date to fetch articles past that date
	 * @param page for recursive calls
	 * @return fetched articles
	 */
	private List<ArticleDTO> updateArticles(String date, int page) {
		List<ArticleDTO> dtos = new ArrayList<>();
		try {
			JsonElement json = this.request(
					"https://engelsburg.smmp.de/wp-json/wp/v2/posts?per_page=100&after=" + date + "&page=" + page); //Parse in article array

			if (json.toString().length() > 2) {
				JsonArray jsonArticles = json.getAsJsonArray();
				for (JsonElement article : jsonArticles) { //Cycle through all articles
					dtos.add(this.createArticleDTO(article.getAsJsonObject().get("id").getAsInt(), article));
					counter++;
				}

				if (jsonArticles.size() == 100) {
					this.updateArticles(date, page + 1).forEach(this.articleController::createOrUpdateArticle);
					LOGGER.info("[ARTICLE] Still fetching articles (current count: " + counter + ")");
				}
			}
		} catch (IOException | ParseException e) {
			this.logError("[ARTICLE] Couldn't fetch", e, LOGGER);
		}
		return dtos;
	}

	/**
	 * Creates an articles dto out of an articleId and a json element.
	 *
	 * @param articleId of dto
	 * @param article   json element with article information
	 * @return parsed ArticleDTO
	 * @throws IOException    if media couldn't be fetched
	 * @throws ParseException if parsing the date threw an exception
	 */
	private ArticleDTO createArticleDTO(int articleId, JsonElement article) throws IOException, ParseException {
		String content = article.getAsJsonObject().get("content").getAsJsonObject().get(
				"rendered").getAsString(); //Get content
		String mediaUrl = null;
		String blurHash = null;

		try {
			mediaUrl = WordPressAPI.getFeaturedMedia(article.getAsJsonObject().get("featured_media").getAsInt(),
					content);
		} catch (Exception e) {
			this.logError("[ARTICLE] Couldn't load media: " + articleId, e, LOGGER);
		}

		if (Environment.PRODUCTION) {
			try {
				//content = WordpressAPI.applyBlurHashToAllImages(Jsoup.parse(content)).toString(); --> not needed
				blurHash = mediaUrl != null ? WordPressAPI.getBlurHash(mediaUrl) : null;
			} catch (IOException e) {
				this.logError("[ARTICLE] Couldn't load blur hash of image", e, LOGGER);
			}
		}

		return new ArticleDTO(//Form ArticleDTO from information crawled
				articleId,
				DATE_FORMAT.parse(article.getAsJsonObject().get("date").getAsString()).getTime(),
				article.getAsJsonObject().get("link").getAsString(),
				article.getAsJsonObject().get("title").getAsJsonObject().get("rendered").getAsString(),
				content,
				Result.hash(content),
				mediaUrl,
				blurHash
		);
	}

	/**
	 * Load articles on start up.
	 */
	public void loadPastArticles() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[ARTICLE] Starting fetching past articles");
		Result<GetArticlesResponseDTO> lastArticle = this.articleController.getArticlesAfter(-1, new Paging(0, 1));
		if (lastArticle.isResultPresent()) { //Empty would be an error
			this.updateArticles(DATE_FORMAT.format(lastArticle.getResult().getArticles().get(0).getDate()), 1)
					.forEach(this.articleController::createOrUpdateArticle);
		} else {
			this.updateArticles(DATE_FORMAT.format(new Date(1000)), 1).forEach(
					this.articleController::createOrUpdateArticle);
		}
		LOGGER.info("[ARTICLE] Fetched " + counter + " past article" + (counter == 1 ? "" : "s") + "!");
		counter = 0;
	}
}
