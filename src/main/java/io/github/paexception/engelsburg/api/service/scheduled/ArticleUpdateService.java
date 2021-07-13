package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.WordpressAPI;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to update articles.
 */
@Service
public class ArticleUpdateService extends LoggingComponent {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	@Autowired
	private ArticleController articleController;
	@Autowired
	private NotificationService notificationService;

	public ArticleUpdateService() {
		super(ArticleUpdateService.class);
	}

	/**
	 * Call {@link #updateArticles(String)} every 15 minutes and return all articles published in that passed 15 minutes.
	 */
	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void fetchNewArticles() {
		this.logger.debug("Starting fetching new articles");
		this.updateArticles(DATE_FORMAT.format(System.currentTimeMillis() - 15 * 60 * 1000)).stream()
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
			DataInputStream input = new DataInputStream(
					new URL("https://engelsburg.smmp.de/wp-json/wp/v2/posts?per_page=100&after=" + date)
							.openConnection().getInputStream()
			); //Fetch all articles after date from the wordpress api of the engelsburg
			String raw = new String(input.readAllBytes());
			if (raw.length() == 2) { //Input equal to "{}" which represents an empty result
				this.logger.debug("No articles found");
				return dtos;
			}

			JsonArray json = JsonParser.parseString(raw).getAsJsonArray(); //Parse in article array
			for (JsonElement article : json) { //Cycle through all articles
				String content = article.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString(); //Get content
				String mediaUrl = WordpressAPI.getFeaturedMedia(article.getAsJsonObject().get("featured_media").getAsInt(), content);
				String blurHash = null;

				try {
					content = WordpressAPI.applyBlurHashToAllImages(Jsoup.parse(content)).toString();
					blurHash = mediaUrl != null ? WordpressAPI.getBlurHash(mediaUrl) : null;
				} catch (IOException e) {
					this.logError("Couldn't load blur hash of image", e);
				}

				ArticleDTO dto = new ArticleDTO(//Form ArticleDTO from information crawled
						-1,
						DATE_FORMAT.parse(article.getAsJsonObject().get("date").getAsString()).getTime(),
						article.getAsJsonObject().get("link").getAsString(),
						article.getAsJsonObject().get("title").getAsJsonObject().get("rendered").getAsString(),
						content,
						mediaUrl,
						blurHash
				);
				dtos.add(dto);
			}

			this.logger.info("Fetched articles");
			if (json.size() == 100) this.updateArticles(json.get(99).getAsJsonObject().get("date").getAsString())
					.forEach(dto -> this.articleController.createArticle(dto));
		} catch (IOException | ParseException e) {
			this.logError("Couldn't fetch articles", e);
		}
		return dtos;
	}

	/**
	 * Load all articles on startup since 1/1/2020.
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void loadPastArticles() {
		this.logger.debug("Starting fetching past articles since 01-01-2020");
		this.articleController.clearAllArticles();
		this.updateArticles("2020-01-01T00:00:00").forEach(dto -> this.articleController.createArticle(dto));
	}

}
