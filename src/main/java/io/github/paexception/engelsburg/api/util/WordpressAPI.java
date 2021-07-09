package io.github.paexception.engelsburg.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordpressAPI {

	/**
	 * Parse the mediaUrl from an wordpress entity.
	 *
	 * @param featuredMedia id if listed
	 * @param content       to search for an alternative img
	 * @return media url
	 * @throws IOException if something goes wrong connecting
	 */
	public static String getFeaturedMedia(int featuredMedia, String content) throws IOException {
		Elements elements;
		if (featuredMedia != 0) { //Featured media listed?
			JsonObject mediaJson = JsonParser.parseReader(new InputStreamReader(
					new URL("https://engelsburg.smmp.de/wp-json/wp/v2/media/" + featuredMedia)
							.openConnection().getInputStream())).getAsJsonObject(); //Then get img url via wordpress api
			return mediaJson.get("source_url").getAsString();
		} else if ((elements = Jsoup.parse(content).getElementsByClass("wp-block-image")).size() > 0) { //If not search for first image in article
			return elements.get(0).getElementsByTag("img").get(0).attr("src");
		}

		return null;
	}

}
