package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.service.JsonFetchingService;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.WordpressAPI;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to update articles.
 */
@Service
public class ArticleUpdateService extends JsonFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleUpdateService.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	@Autowired
	private ArticleController articleController;
	@Autowired
	private NotificationService notificationService;

	/**
	 * Call {@link #updateArticles(String)} every 15 minutes and return all articles published in that passed 1 minute.
	 */
	@Scheduled(fixedRate = 60 * 1000)
	public void fetchNewArticles() {
		LOGGER.debug("Starting fetching new articles");
		this.updateArticles(DATE_FORMAT.format(System.currentTimeMillis() - 60 * 1000)).stream()
				.peek(dto -> this.notificationService.sendArticleNotifications(dto))
				.forEach(dto -> this.articleController.createArticle(dto));
	}

	/**
	 * Fetch all articles past a specific date/time.
	 *
	 * @param date to fetch articles past that date
	 * @return fetched articles
	 */
	private List<ArticleDTO> updateArticles(String date) {
		List<ArticleDTO> dtos = new ArrayList<>();
		try {
			JsonElement json = this.request("https://engelsburg.smmp.de/wp-json/wp/v2/posts?per_page=100&after=" + date); //Parse in article array

			if (json.toString().length() > 2) {
				JsonArray jsonArticles = json.getAsJsonArray();
				for (JsonElement article : jsonArticles) { //Cycle through all articles
					String content = article.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString(); //Get content
					String mediaUrl = WordpressAPI.getFeaturedMedia(article.getAsJsonObject().get("featured_media").getAsInt(), content);
					String blurHash = null;

					if (Environment.PRODUCTION) {
						try {
							content = WordpressAPI.applyBlurHashToAllImages(Jsoup.parse(content)).toString();
							blurHash = mediaUrl != null ? WordpressAPI.getBlurHash(mediaUrl) : null;
						} catch (IOException e) {
							this.logError("Couldn't load blur hash of image", e, LOGGER);
						}
					}

					ArticleDTO dto = new ArticleDTO(//Form ArticleDTO from information crawled
							-1,
							DATE_FORMAT.parse(article.getAsJsonObject().get("date").getAsString()).getTime(),
							article.getAsJsonObject().get("link").getAsString(),
							article.getAsJsonObject().get("title").getAsJsonObject().get("rendered").getAsString(),
							content,
							mediaUrl,
							blurHash,
							0
					);
					dtos.add(dto);
				}

				LOGGER.info("Fetched articles");
				if (jsonArticles.size() == 100)
					this.updateArticles(jsonArticles.get(99).getAsJsonObject().get("date").getAsString())
							.forEach(dto -> this.articleController.createArticle(dto));
			} else LOGGER.debug("No articles found");
		} catch (IOException | ParseException e) {
			this.logError("Couldn't fetch articles", e, LOGGER);
		}
		return dtos;
	}

	/**
	 * Load all articles on startup since 1/1/2020.
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void loadPastArticles() {
		LOGGER.debug("Starting fetching past articles since 01-01-2020");
		this.articleController.clearAllArticles();
		this.updateArticles("2020-01-01T00:00:00").forEach(dto -> this.articleController.createArticle(dto));
	}

}
