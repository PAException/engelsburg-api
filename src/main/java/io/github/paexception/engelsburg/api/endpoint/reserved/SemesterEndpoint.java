/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SemesterController;
import io.github.paexception.engelsburg.api.endpoint.dto.SemesterDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSemestersResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.Min;


/**
 * RestController for semester actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/semester")
public class SemesterEndpoint {

	private final SemesterController semesterController;

	/**
	 * Create a new semester.
	 *
	 * @see SemesterController#createSemester(CreateSemesterRequestDTO, UserDTO)
	 */
	@AuthScope("semester.write.self")
	@PostMapping
	@Response(SemesterDTO.class)
	private Object createSemester(@RequestBody @Valid CreateSemesterRequestDTO dto, UserDTO userDTO) {
		return this.semesterController.createSemester(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update existing semester.
	 *
	 * @see SemesterController#updateSemester(UpdateSemesterRequestDTO, UserDTO)
	 */
	@AuthScope("semester.write.self")
	@PatchMapping
	@Response(SemesterDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "semester")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "semester")
	private Object updateSemester(@RequestBody @Valid UpdateSemesterRequestDTO dto, UserDTO userDTO) {
		return this.semesterController.updateSemester(dto, userDTO).getHttpResponse();
	}


	/**
	 * Get semester by id.
	 *
	 * @see SemesterController#getSemester(int, UserDTO)
	 */
	@AuthScope("semester.read.self")
	@GetMapping("/{semesterId}")
	@Response(SemesterDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "semester")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "semester")
	private Object getSemester(@PathVariable @Min(1) @Schema(example = "43") int semesterId, UserDTO userDTO) {
		return this.semesterController.getSemester(semesterId, userDTO).getHttpResponse();
	}

	/**
	 * Get all semesters of user.
	 *
	 * @see SemesterController#getAllSemester(UserDTO)
	 */
	@AuthScope("semester.read.self")
	@GetMapping
	@Response(GetSemestersResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "semester")
	private Object getAllSemesters(UserDTO userDTO) {
		return this.semesterController.getAllSemester(userDTO).getHttpResponse();
	}

	/**
	 * Delete a semester.
	 *
	 * @see SemesterController#deleteSemester(int, UserDTO)
	 */
	@AuthScope("semester.delete.self")
	@DeleteMapping("/{semesterId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "semester")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "semester")
	@ErrorResponse(status = 424, messageKey = "FAILED_DEPENDENCY", extra = "semester", description = "Semester cannot be deleted because another entity is depending on it")
	private Object deleteSemester(@PathVariable @Min(1) @Schema(example = "43") int semesterId, UserDTO userDTO) {
		return this.semesterController.deleteSemester(semesterId, userDTO).getHttpResponse();
	}
}
