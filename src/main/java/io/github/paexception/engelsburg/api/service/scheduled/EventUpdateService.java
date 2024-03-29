/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonElement;
import io.github.paexception.engelsburg.api.controller.shared.EventController;
import io.github.paexception.engelsburg.api.endpoint.dto.EventDTO;
import io.github.paexception.engelsburg.api.service.JsonFetchingService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to update articles.
 */
@Service
@AllArgsConstructor
public class EventUpdateService extends JsonFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdateService.class);
	private final EventController eventController;

	/**
	 * Scheduled function to update events every hour.
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void updateEvents() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[EVENT] Fetching...");
		try {
			JsonElement content = this.request(
					"https://engelsburg.smmp.de/wp-json/wp/v2/pages/318").getAsJsonObject().get(
					"content").getAsJsonObject().get("rendered");

			if (this.checkChanges(content)) {
				List<EventDTO> dtos = new ArrayList<>();
				Element list = Jsoup.parse(content.getAsString()).getElementsByTag(
						"ul").first(); //Select event container
				list.children().forEach(element -> { //iterate through events
					if (element.getElementsByTag("a").first() != null)
						dtos.add(new EventDTO(this.parseDate(element.text()),
								element.getElementsByTag("a").first().text()));
				});

				this.eventController.clearAllEvents();
				dtos.forEach(this.eventController::createEvent);
				LOGGER.info("[EVENT] Updated");
			} else LOGGER.debug("[EVENT] Not changed");
		} catch (IOException e) {
			this.logExpectedError("[EVENT] Couldn't fetch", e, LOGGER);
		}
	}

	/**
	 * Parse dates of the engelsburg website properly.
	 *
	 * @param toParse String to parse
	 * @return parsed Date
	 */
	private Date parseDate(String toParse) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

			return new Date(simpleDateFormat.parse(toParse.split(":")[0]).getTime());
		} catch (ParseException ignored) {
			return new Date(System.currentTimeMillis());
		}
	}

}
