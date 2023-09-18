/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.InformationController;
import io.github.paexception.engelsburg.api.endpoint.dto.TeacherDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTeachersResponseDTO;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for information actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/info")
public class InformationEndpoint {

	private final InformationController informationController;

	/**
	 * Get information about a specific teacher.
	 *
	 * @see InformationController#getTeacher(String)
	 */
	@GetMapping("/teacher/{abbreviation}")
	@Response(TeacherDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "information")
	public Object getTeacher(
			@RequestParam @Schema(example = "<substituteKey>") String substituteKey,
			@PathVariable("abbreviation") String abbreviation) {
		if (!Environment.SCHOOL_TOKEN.equals(substituteKey))
			return Result.of(Error.FORBIDDEN, "information").getHttpResponse();

		return this.informationController.getTeacher(abbreviation).getHttpResponse();
	}

	/**
	 * Get all current classes.
	 *
	 * @see InformationController#getCurrentClasses()
	 */
	@GetMapping("/classes")
	@Response(GetClassesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "information")
	public Object getCurrentClasses(@RequestParam @Schema(example = "<substituteKey>") String substituteKey) {
		if (!Environment.SCHOOL_TOKEN.equals(substituteKey))
			return Result.of(Error.FORBIDDEN, "information").getHttpResponse();

		return this.informationController.getCurrentClasses().getHttpResponse();
	}

	/**
	 * Get all known teachers.
	 *
	 * @see InformationController#getAllTeachers()
	 */
	@GetMapping("/teacher")
	@Response(GetTeachersResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "information")
	public Object getTeachers(@RequestParam @Schema(example = "<substituteKey>") String substituteKey) {
		if (!Environment.SCHOOL_TOKEN.equals(substituteKey))
			return Result.of(Error.FORBIDDEN, "information").getHttpResponse();

		return this.informationController.getAllTeachers().getHttpResponse();
	}

}
