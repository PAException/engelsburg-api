package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventEndpoint {

	@Autowired private EventController eventController;

	@GetMapping("/event")
	public Object getAllEvents() {
		return this.eventController.getAllEvents().getHttpResponse();
	}

}
