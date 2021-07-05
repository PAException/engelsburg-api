package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.EventController;
import io.github.paexception.engelsburg.api.endpoint.dto.EventDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class EventUpdateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdateService.class.getSimpleName());
	@Autowired
	private EventController eventController;

	/**
	 * Scheduled function to update events every hour.
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void updateEvents() {
		LOGGER.debug("Starting fetching substitutes");
		try {
			Document doc = Jsoup.connect("https://engelsburg.smmp.de/organisation/termine/").get();

			List<EventDTO> dtos = new ArrayList<>();
			Element list = doc.select("#genesis-content > article > div.entry-content > ul.navlist").first(); //Select event container
			list.getElementsByTag("li").forEach(element -> { //iterate through events
				dtos.add(new EventDTO(this.parseDate(element.text()), element.getElementsByTag("a").first().text()));
			});

			this.eventController.clearAllEvents();
			dtos.forEach(dto -> this.eventController.createEvent(dto));
			LOGGER.info("Fetched events");
		} catch (IOException e) {
			LOGGER.error("Couldn't fetch events", e);
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
