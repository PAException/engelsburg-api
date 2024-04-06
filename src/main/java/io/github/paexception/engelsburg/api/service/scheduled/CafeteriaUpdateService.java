/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonObject;
import io.github.paexception.engelsburg.api.controller.shared.CafeteriaController;
import io.github.paexception.engelsburg.api.endpoint.dto.CafeteriaInformationDTO;
import io.github.paexception.engelsburg.api.service.JsonFetchingService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.WordPressAPI;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class CafeteriaUpdateService extends JsonFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(CafeteriaUpdateService.class);
	private final CafeteriaController cafeteriaController;

	/**
	 * Scheduled function to update cafeteria information.
	 */
	@Scheduled(fixedRate = 2 * 60 * 1000)
	public void updateCafeteriaInformation() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		try {
			LOGGER.debug("[CAFETERIA] Fetching...");
			JsonObject json = this.request("https://engelsburg.smmp.de/wp-json/wp/v2/pages/635").getAsJsonObject();
			if (json.get("id") == null) return;

			if (this.checkChanges(json)) {
				String link = json.get("link").getAsString();
				String content = json.get("content").getAsJsonObject().get("rendered").getAsString();
				String mediaUrl = WordPressAPI.getFeaturedMedia(json.get("featured_media").getAsInt(), content);
				String blurHash = null;

				try {
					content = WordPressAPI.applyBlurHashToAllImages(Jsoup.parse(content)).toString();
					blurHash = mediaUrl != null ? WordPressAPI.getBlurHash(mediaUrl) : null;
				} catch (IOException e) {
					this.logExpectedError("[CAFETERIA] Couldn't load blur hash of image", e, LOGGER);
				}

				this.cafeteriaController.update(new CafeteriaInformationDTO(content, link, mediaUrl, blurHash));
				LOGGER.info("[CAFETERIA] Updated");
			} else LOGGER.debug("[CAFETERIA] Not changed");
		} catch (IOException e) {
			this.logExpectedError("[CAFETERIA] Couldn't fetch", e, LOGGER);
		}
	}

}
