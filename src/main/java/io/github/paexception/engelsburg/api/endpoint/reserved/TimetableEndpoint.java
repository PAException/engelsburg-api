/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.TimetableController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTimetableEntriesResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for timetable actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/timetable")
public class TimetableEndpoint {

	private final TimetableController timetableController;

	/**
	 * Set a timetable entry.
	 *
	 * @see TimetableController#setTimetableEntry(TimetableDTO, UserDTO, SemesterModel)
	 */
	@AuthScope("timetable.write.self")
	@PostMapping
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to get that subject")
	public Object setTimetableEntry(@RequestBody @Valid TimetableDTO dto, UserDTO userDTO, SemesterModel semester) {
		return this.timetableController.setTimetableEntry(dto, userDTO, semester).getHttpResponse();
	}

	/**
	 * Get timetable entries.
	 *
	 * @see TimetableController#getTimetableEntries(int, int, SemesterModel)
	 */
	@AuthScope("timetable.read.self")
	@GetMapping
	@Response(GetTimetableEntriesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "timetable")
	public Object getTimetableEntries(
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "2") int day,
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "5") int lesson,
			SemesterModel semester) {
		return this.timetableController.getTimetableEntries(day, lesson, semester).getHttpResponse();
	}

	/**
	 * Delete a timetable entry.
	 *
	 * @see TimetableController#deleteTimetableEntry(int, int, SemesterModel)
	 */
	@AuthScope("timetable.delete.self")
	@DeleteMapping
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "timetable")
	public Object deleteTimetableEntry(
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "2") int day,
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "5") int lesson,
			SemesterModel semester) {
		return this.timetableController.deleteTimetableEntry(day, lesson, semester).getHttpResponse();
	}
}
