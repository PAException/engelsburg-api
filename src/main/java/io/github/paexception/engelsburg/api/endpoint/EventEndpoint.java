package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.authorization.interceptor.IgnoreServiceToken;
import io.github.paexception.engelsburg.api.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for event actions
 */
@IgnoreServiceToken
@RestController
public class EventEndpoint {

	@Autowired
	private EventController eventController;

	/**
	 * Return all events
	 *
	 * @see EventController#getAllEvents()
	 */
	@GetMapping("/event")
	public Object getAllEvents() {
		return this.eventController.getAllEvents().getHttpResponse();
	}

}
