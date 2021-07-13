package io.github.paexception.engelsburg.api.endpoint.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.reserved.TimetableController;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.DeleteTimetableEntryRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetTimetableEntriesRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for timetable actions.
 */
@RestController
public class TimetableEndpoint {

	@Autowired
	private TimetableController timetableController;

	/**
	 * Set a timetable entry.
	 *
	 * @see TimetableController#setTimetableEntry(TimetableDTO, DecodedJWT)
	 */
	@AuthScope("timetable.write.self")
	@PostMapping("/timetable")
	public Object setTimetableEntry(@RequestBody @Valid TimetableDTO dto, DecodedJWT jwt) {
		return this.timetableController.setTimetableEntry(dto, jwt).getHttpResponse();
	}

	/**
	 * Get timetable entries.
	 *
	 * @see TimetableController#getTimetableEntries(GetTimetableEntriesRequestDTO, DecodedJWT)
	 */
	@AuthScope("timetable.read.self")
	@GetMapping("/timetable")
	public Object getTimetableEntries(@RequestBody GetTimetableEntriesRequestDTO dto, DecodedJWT jwt) {
		return this.timetableController.getTimetableEntries(dto, jwt).getHttpResponse();
	}

	/**
	 * Delete a timetable entry.
	 *
	 * @see TimetableController#deleteTimetableEntry(DeleteTimetableEntryRequestDTO, DecodedJWT)
	 */
	@AuthScope("timetable.delete.self")
	@DeleteMapping("/timetable")
	public Object deleteTimetableEntry(@RequestBody @Valid DeleteTimetableEntryRequestDTO dto, DecodedJWT jwt) {
		return this.timetableController.deleteTimetableEntry(dto, jwt).getHttpResponse();
	}

}
