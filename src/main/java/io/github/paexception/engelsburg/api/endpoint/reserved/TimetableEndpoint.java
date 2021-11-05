package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.TimetableController;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.DeleteTimetableEntryRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for timetable actions.
 */
@RestController
public class TimetableEndpoint {

	private final TimetableController timetableController;

	public TimetableEndpoint(TimetableController timetableController) {
		this.timetableController = timetableController;
	}

	/**
	 * Set a timetable entry.
	 *
	 * @see TimetableController#setTimetableEntry(TimetableDTO, UserDTO)
	 */
	@AuthScope("timetable.write.self")
	@PostMapping("/timetable")
	public Object setTimetableEntry(@RequestBody @Valid TimetableDTO dto, UserDTO userDTO) {
		return this.timetableController.setTimetableEntry(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get timetable entries.
	 *
	 * @see TimetableController#getTimetableEntries(int, int, UserDTO)
	 */
	@AuthScope("timetable.read.self")
	@GetMapping("/timetable")
	public Object getTimetableEntries(@RequestParam(required = false, defaultValue = "-1") int day,
			@RequestParam(required = false, defaultValue = "-1") int lesson, UserDTO userDTO) {
		return this.timetableController.getTimetableEntries(day, lesson, userDTO).getHttpResponse();
	}

	/**
	 * Delete a timetable entry.
	 *
	 * @see TimetableController#deleteTimetableEntry(DeleteTimetableEntryRequestDTO, UserDTO)
	 */
	@AuthScope("timetable.delete.self")
	@DeleteMapping("/timetable")
	public Object deleteTimetableEntry(@RequestBody @Valid DeleteTimetableEntryRequestDTO dto, UserDTO userDTO) {
		return this.timetableController.deleteTimetableEntry(dto, userDTO).getHttpResponse();
	}

}
