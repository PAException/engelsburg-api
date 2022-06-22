/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.EventController;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetEventsResponseDTO;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for event actions.
 */
@RestController
@AllArgsConstructor
@Tag(name = "other")
public class EventEndpoint {

	private final EventController eventController;

	/**
	 * Return all events.
	 *
	 * @see EventController#getAllEvents()
	 */
	@GetMapping("/event")
	@Response(GetEventsResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "event")
	public Object getAllEvents() {
		return this.eventController.getAllEvents().getHttpResponse();
	}
}
