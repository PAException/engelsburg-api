package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.controller.ArticleController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to update articles
 */
@Service
public class ArticleUpdateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleUpdateService.class.getSimpleName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	@Autowired
	private ArticleController articleController;

	/**
	 * Call {@link #updateArticles(String)} every 15 minutes and return all articles published in that passed 15 minutes
	 */
	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void fetchNewArticles() {
		this.updateArticles(dateFormat.format(System.currentTimeMillis() - 15 * 60 * 1000));
	}

	/**
	 * Fetch all articles past a specific date/time
	 *
	 * @param date to fetch articles past that date
	 */
	private void updateArticles(String date) {
		try {
			LOGGER.debug("Starting fetching articles");

			DataInputStream input = new DataInputStream(
					new URL("https://engelsburg.smmp.de/wp-json/wp/v2/posts?per_page=100&after=" + date)
							.openConnection().getInputStream()
			);//Fetch all articles after date from the wordpress api of the engelsburg
			String raw = new String(input.readAllBytes());
			if (raw.length() == 2) {//Input equal to "{}" which represents an empty result
				LOGGER.debug("No new articles found");
				return;
			}

			JsonArray json = JsonParser.parseString(raw).getAsJsonArray();//Parse in article array
			List<ArticleDTO> dtos = new ArrayList<>();
			for (JsonElement article : json) {//Cycle through all articles
				String content = article.getAsJsonObject().get("content").getAsJsonObject().get("rendered").getAsString();//Get content
				Elements elements;
				String mediaUrl = null;
				int featuredMedia;
				if ((featuredMedia = article.getAsJsonObject().get("featured_media").getAsInt()) != 0) {//Featured media listed?
					JsonObject mediaJson = JsonParser.parseReader(new InputStreamReader(
							new URL("https://engelsburg.smmp.de/wp-json/wp/v2/media/" + featuredMedia)
									.openConnection().getInputStream())).getAsJsonObject();//Then get img url via wordpress api
					mediaUrl = mediaJson.get("source_url").getAsString();
				} else if ((elements = Jsoup.parse(content).getElementsByClass("wp-block-image")).size() > 0) {//If not search for first image in article
					mediaUrl = elements.get(0).getElementsByTag("img").get(0).attr("src");
				}
				ArticleDTO dto = new ArticleDTO(//Form ArticleDTO from information crawled
						dateFormat.parse(article.getAsJsonObject().get("date").getAsString()).getTime(),
						article.getAsJsonObject().get("link").getAsString(),
						article.getAsJsonObject().get("title").getAsJsonObject().get("rendered").getAsString(),
						content,
						mediaUrl
				);
				dtos.add(dto);
			}
			dtos.forEach(dto -> this.articleController.createArticle(dto));

			LOGGER.info("Fetched articles");
			if (json.size() == 100) this.updateArticles(json.get(99).getAsJsonObject().get("date").getAsString());
		} catch (IOException | ParseException e) {
			LOGGER.error("Couldn't load articles from homepage", e);
		}
	}

	/**
	 * Load all articles on startup since 1/1/2020
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void loadPastArticles() {
		this.articleController.clearAllArticles();
		this.updateArticles("2020-01-01T00:00:00");
	}

}
