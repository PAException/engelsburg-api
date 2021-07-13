package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.controller.shared.EventController;
import io.github.paexception.engelsburg.api.endpoint.dto.EventDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to update articles.
 */
@Service
public class EventUpdateService extends LoggingComponent {

	@Autowired
	private EventController eventController;

	public EventUpdateService() {
		super(EventUpdateService.class);
	}

	/**
	 * Scheduled function to update events every hour.
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void updateEvents() {
		this.logger.debug("Starting to fetch events");
		try {
			DataInputStream input = new DataInputStream(
					new URL("https://engelsburg.smmp.de/wp-json/wp/v2/pages/318")
							.openConnection().getInputStream());
			JsonElement content = JsonParser.parseString(new String(input.readAllBytes()))
					.getAsJsonObject().get("content").getAsJsonObject().get("rendered");

			List<EventDTO> dtos = new ArrayList<>();
			Element list = Jsoup.parse(content.getAsString()).getElementsByTag("ul").first(); //Select event container
			list.children().forEach(element -> { //iterate through events
				if (element.getElementsByTag("a").first() != null)
					dtos.add(new EventDTO(this.parseDate(element.text()), element.getElementsByTag("a").first().text()));
			});

			this.eventController.clearAllEvents();
			dtos.forEach(dto -> this.eventController.createEvent(dto));
			this.logger.info("Updated events");
		} catch (IOException e) {
			this.logError("Couldn't fetch events", e);
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
