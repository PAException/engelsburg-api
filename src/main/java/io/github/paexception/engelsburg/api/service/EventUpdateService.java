package io.github.paexception.engelsburg.api.service;

import io.github.paexception.engelsburg.api.controller.EventController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateEventRequestDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventUpdateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdateService.class.getSimpleName());
	@Autowired private EventController eventController;

	@Scheduled(fixedRate = 60*60*1000)
	public void updateEvents() {
		LOGGER.debug("Starting fetching substitutes");
		try {
			Document doc = Jsoup.connect("https://engelsburg.smmp.de/organisation/termine/").get();

			List<CreateEventRequestDTO> dtos = new ArrayList<>();
			Element list = doc.select("#genesis-content > article > div.entry-content > ul.navlist").first();
			list.getElementsByTag("li").forEach(element -> {
				dtos.add(new CreateEventRequestDTO(this.parseDate(element.text()), element.getElementsByTag("a").first().text()));
			});

			this.eventController.clearAllEvents();
			dtos.forEach(dto -> this.eventController.createEvent(dto));
			LOGGER.info("Fetched events");
		} catch (IOException e) {
			LOGGER.error("Couldn't fetch events", e);
		}
	}

	private Date parseDate(String toParse) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

			return new Date(simpleDateFormat.parse(toParse.substring(1, toParse.length()-3)).getTime());
		} catch (ParseException ignored) {
			return new Date(System.currentTimeMillis());
		}
	}

}
