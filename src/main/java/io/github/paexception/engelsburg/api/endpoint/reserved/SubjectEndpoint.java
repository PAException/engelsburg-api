/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SubjectController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.SubjectDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubjectRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateSubjectRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.BaseSubjectsResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubjectsResponseDTO;
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
 * RestController for subject actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/subject")
public class SubjectEndpoint {

	private final SubjectController subjectController;

	/**
	 * Create a new subject.
	 *
	 * @see SubjectController#createSubject(CreateSubjectRequestDTO, SemesterModel)
	 */
	@AuthScope("subject.write.self")
	@PostMapping
	@Response(SubjectDTO.class)
	@ErrorResponse(status = 409, messageKey = "ALREADY_EXISTS", extra = "subject")
	public Object createSubject(@RequestBody @Valid CreateSubjectRequestDTO dto, SemesterModel semester) {
		return this.subjectController.createSubject(dto, semester).getHttpResponse();
	}

	/**
	 * Update a subject.
	 *
	 * @see SubjectController#updateSubject(UpdateSubjectRequestDTO, UserDTO)
	 */
	@AuthScope("subject.write.self")
	@PatchMapping
	@Response(SubjectDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject")
	public Object updateSubject(@RequestBody @Valid UpdateSubjectRequestDTO dto, UserDTO userDTO) {
		return this.subjectController.updateSubject(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get a subject.
	 *
	 * @see SubjectController#getSubject(int, UserDTO)
	 */
	@AuthScope("subject.read.self")
	@GetMapping("/{subjectId}")
	@Response(SubjectDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject")
	public Object getSubject(@PathVariable @Min(1) @Schema(example = "1423") int subjectId, UserDTO userDTO) {
		return this.subjectController.getSubject(subjectId, userDTO).getHttpResponse();
	}

	/**
	 * Get all subjects of user.
	 *
	 * @see SubjectController#getAllSubjects(SemesterModel)
	 */
	@AuthScope("subject.read.self")
	@GetMapping
	@Response(SubjectsResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	public Object getAllSubjects(SemesterModel semester) {
		return this.subjectController.getAllSubjects(semester).getHttpResponse();
	}

	/**
	 * Delete a subject.
	 *
	 * @see SubjectController#deleteSubject(int, UserDTO)
	 */
	@AuthScope("subject.delete.self")
	@DeleteMapping("/{subjectId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject")
	@ErrorResponse(status = 424, messageKey = "FAILED_DEPENDENCY", extra = "subject", description = "Subject cannot be deleted because another entity is depending on it")
	public Object deleteSubject(@PathVariable @Min(1) @Schema(example = "1423") int subjectId, UserDTO userDTO) {
		return this.subjectController.deleteSubject(subjectId, userDTO).getHttpResponse();
	}

	/**
	 * Get all base subjects.
	 *
	 * @see SubjectController#getBaseSubjects()
	 */
	@GetMapping("/base")
	@Response(BaseSubjectsResponseDTO.class)
	public Object getBaseSubjects() {
		return this.subjectController.getBaseSubjects().getHttpResponse();
	}
}
