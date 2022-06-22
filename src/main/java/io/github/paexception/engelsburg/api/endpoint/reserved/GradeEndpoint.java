/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.GradeController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradesResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * RestController for grade actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/grade")
public class GradeEndpoint {

	private final GradeController gradeController;

	/**
	 * Create a new grade.
	 *
	 * @see GradeController#createGrade(CreateGradeRequestDTO, UserDTO)
	 */
	@AuthScope("grade.write.self")
	@PostMapping
	@Response(GradeDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade_share")
	public Object createGrade(@RequestBody @Valid CreateGradeRequestDTO dto, UserDTO userDTO) {
		return this.gradeController.createGrade(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update a grade.
	 *
	 * @see GradeController#updateGrade(UpdateGradeRequestDTO, UserDTO)
	 */
	@AuthScope("grade.write.self")
	@PatchMapping
	@Response(GradeDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade", key = "Grade")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share", key = "Grade share")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade", key = "Grade")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade_share", key = "Grade share")
	public Object updateGrade(@RequestBody @Valid UpdateGradeRequestDTO dto, UserDTO userDTO) {
		return this.gradeController.updateGrade(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get all grades or by subject.
	 *
	 * @see GradeController#getGrades(int, UserDTO, SemesterModel)
	 */
	@AuthScope("grade.read.self")
	@GetMapping
	@Response(GetGradesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade", key = "Grade")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share", key = "Grade share")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject", key = "Subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to get that subject")
	public Object getGrades(
			@RequestParam(defaultValue = "-1", required = false) @Schema(example = "1423") int subjectId,
			UserDTO userDTO, SemesterModel semester) {
		return this.gradeController.getGrades(subjectId, userDTO, semester).getHttpResponse();
	}

	/**
	 * Delete a grade by gradeId.
	 *
	 * @see GradeController#deleteGrade(int, UserDTO)
	 */
	@AuthScope("grade.delete.self")
	@DeleteMapping("/{gradeId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade", description = "If the user has not the permission to delete that grade")
	@ErrorResponse(status = 424, messageKey = "FAILED_DEPENDENCY", extra = "grade", description = "Grade cannot be deleted because another entity is depending on it")
	public Object deleteGrade(@PathVariable @Min(1) @Schema(example = "11") int gradeId, UserDTO userDTO) {
		return this.gradeController.deleteGrade(gradeId, userDTO).getHttpResponse();
	}
}
