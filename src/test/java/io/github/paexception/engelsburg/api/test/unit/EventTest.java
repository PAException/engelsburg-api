package io.github.paexception.engelsburg.api.test.unit;

import io.github.paexception.engelsburg.api.controller.shared.EventController;
import io.github.paexception.engelsburg.api.database.model.EventModel;
import io.github.paexception.engelsburg.api.database.repository.EventRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetEventsResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.sql.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventTest {

	private static final EventModel TEST_EVENT = new EventModel(-1, new Date(0), "test_title");

	@InjectMocks
	private EventController eventController;

	@Mock
	private EventRepository eventRepository;

	@Test
	public void noData() {
		Result<GetEventsResponseDTO> dto = this.eventController.getAllEvents();

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("event");
	}

	@Test
	public void create() {
		this.eventController.createEvent(TEST_EVENT.toResponseDTO());

		ArgumentCaptor<EventModel> event = ArgumentCaptor.forClass(EventModel.class);
		verify(this.eventRepository).save(event.capture());

		assertThat(event.getValue().getTitle()).isEqualTo(TEST_EVENT.getTitle());
		assertThat(event.getValue().getDate()).isEqualTo(TEST_EVENT.getDate());
	}

	@Test
	public void delete() {
		this.eventController.clearAllEvents();

		verify(this.eventRepository).deleteAll();
	}

	@Test
	public void success() {
		when(this.eventRepository.findAll()).thenReturn(List.of(TEST_EVENT));

		Result<GetEventsResponseDTO> dto = this.eventController.getAllEvents();

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult().getEvents().size()).isEqualTo(1);
		assertThat(dto.getResult().getEvents().get(0).getTitle()).isEqualTo(TEST_EVENT.getTitle());
		assertThat(dto.getResult().getEvents().get(0).getDate()).isEqualTo(TEST_EVENT.getDate());
	}
}
