/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.InformationController;
import io.github.paexception.engelsburg.api.endpoint.dto.TeacherDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTeachersResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
	@AuthScope("info.teacher.read.all")
	@GetMapping("/teacher/{abbreviation}")
	@Response(TeacherDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	public Object getTeacher(@PathVariable("abbreviation") String abbreviation) {
		return this.informationController.getTeacher(abbreviation).getHttpResponse();
	}

	/**
	 * Get all current classes.
	 *
	 * @see InformationController#getCurrentClasses()
	 */
	@AuthScope("info.classes.read.all")
	@GetMapping("/classes")
	@Response(GetClassesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	public Object getCurrentClasses() {
		return this.informationController.getCurrentClasses().getHttpResponse();
	}

	/**
	 * Get all known teachers.
	 *
	 * @see InformationController#getAllTeachers()
	 */
	@AuthScope("info.teacher.read.all")
	@GetMapping("/teacher")
	@Response(GetTeachersResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "information")
	public Object getTeachers() {
		return this.informationController.getAllTeachers().getHttpResponse();
	}

}
