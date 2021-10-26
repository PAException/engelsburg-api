package io.github.paexception.engelsburg.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.trbl.blurhash.BlurHash;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordpressAPI {

	/**
	 * Apply blur hashes to all images in html attributes.
	 *
	 * @param element to apply
	 * @return this element
	 * @throws IOException if image couldn't be read
	 */
	public static Element applyBlurHashToAllImages(Element element) throws IOException {
		for (Element img : element.getElementsByTag("img"))
			img.attr("blurHash", getBlurHash(img.attr("src")));

		return element;
	}

	/**
	 * Get the blur hash of an image.
	 *
	 * @param imageUrl to read
	 * @return the blur hash
	 * @throws IOException the image couldn't be read
	 */
	public static String getBlurHash(String imageUrl) throws IOException {
		BufferedImage image = ImageIO.read(new URL(imageUrl));

		return BlurHash.encode(image);
	}

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
