package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.EventController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for event actions.
 */
@RestController
public class EventEndpoint {

	private final EventController eventController;

	public EventEndpoint(EventController eventController) {
		this.eventController = eventController;
	}

	/**
	 * Return all events.
	 *
	 * @see EventController#getAllEvents()
	 */
	@GetMapping("/event")
	public Object getAllEvents() {
		return this.eventController.getAllEvents().getHttpResponse();
	}

}
