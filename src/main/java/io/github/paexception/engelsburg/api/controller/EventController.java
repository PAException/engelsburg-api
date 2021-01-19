package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.model.EventModel;
import io.github.paexception.engelsburg.api.database.repository.EventRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateEventRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.EventResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetEventsResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for events
 */
@Component
public class EventController {

	@Autowired private EventRepository eventRepository;

	/**
	 * Create a new event
	 * @param dto which has article information
	 */
	public void createEvent(CreateEventRequestDTO dto) {
		this.eventRepository.save(new EventModel(-1, dto.getDate(), dto.getTitle()));
	}

	/**
	 * Get all events
	 * @return found events in DTO
	 */
	public Result<GetEventsResponseDTO> getAllEvents() {
		List<EventResponseDTO> responseDTOs = new ArrayList<>();
		this.eventRepository.findAll().forEach(event -> responseDTOs.add(event.toResponseDTO()));
		if (responseDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, "events");

		return Result.of(new GetEventsResponseDTO(responseDTOs));
	}

	/**
	 * Delete all events
	 * Only {@link io.github.paexception.engelsburg.api.service.EventUpdateService} is supposed to call
	 * this function!
	 */
	@Transactional
	public void clearAllEvents() {
		this.eventRepository.deleteAll();
	}

}
