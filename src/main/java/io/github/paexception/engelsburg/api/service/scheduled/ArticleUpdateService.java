package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.service.JsonFetchingService;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.WordpressAPI;
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
	private static int counter = 0;
	@Autowired
	private ArticleController articleController;
	@Autowired
	private NotificationService notificationService;

	/**
	 * Call {@link #updateArticles(String, int)} every 15 minutes and return all articles published in that passed 1 minute.
	 */
	@Scheduled(fixedRate = 60 * 1000)
	public void fetchNewArticles() {
		LOGGER.debug("Starting fetching new articles");
		this.updateArticles(DATE_FORMAT.format(System.currentTimeMillis() - 60 * 1000), 1).stream()
				.peek(dto -> this.notificationService.sendArticleNotifications(dto))
				.forEach(dto -> this.articleController.createArticle(dto));
	}

	/**
	 * Checks for updates of articles every 30 minutes.
	 */
	@Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 30 * 60 * 1000)
	public void checkIfArticlesChanged() {
		this.articleController.prepareArticleToUpdate().forEach(idAndHash -> {
			try {
				JsonElement json = this.request("https://engelsburg.smmp.de/wp-json/wp/v2/posts/" + idAndHash.getArticleId());
				String content = json.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString();
				if (!idAndHash.getContentHash().equals(Result.hash(content))) {
					this.articleController.updateArticle(this.createArticleDTO(idAndHash.getArticleId(), json));
				}
			} catch (Exception ignored) {
			}
		});
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
			JsonElement json = this.request("https://engelsburg.smmp.de/wp-json/wp/v2/posts?per_page=100&after=" + date + "&page=" + page); //Parse in article array

			if (json.toString().length() > 2) {
				JsonArray jsonArticles = json.getAsJsonArray();
				for (JsonElement article : jsonArticles) { //Cycle through all articles
					dtos.add(this.createArticleDTO(article.getAsJsonObject().get("id").getAsInt(), article));
					counter++;
				}

				LOGGER.info("Fetched articles");
				if (jsonArticles.size() == 100)
					this.updateArticles(date, page + 1).forEach(dto -> this.articleController.createArticle(dto));
			} else LOGGER.debug("No articles found");
		} catch (IOException | ParseException e) {
			this.logError("Couldn't fetch articles", e, LOGGER);
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
		String content = article.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString(); //Get content
		String mediaUrl = WordpressAPI.getFeaturedMedia(article.getAsJsonObject().get("featured_media").getAsInt(), content);
		String blurHash = null;

		if (Environment.PRODUCTION) {
			try {
				//content = WordpressAPI.applyBlurHashToAllImages(Jsoup.parse(content)).toString(); --> not needed
				blurHash = mediaUrl != null ? WordpressAPI.getBlurHash(mediaUrl) : null;
			} catch (IOException e) {
				this.logError("Couldn't load blur hash of image", e, LOGGER);
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
	 * Load all articles on startup since 1/1/2020.
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void loadPastArticles() {
		LOGGER.debug("Starting fetching past articles");
		this.articleController.clearAllArticles();
		this.updateArticles("1970-01-01T00:00:01", 1).forEach(dto -> this.articleController.createArticle(dto));
		LOGGER.info("Fetched " + counter + " articles!");
	}

}
