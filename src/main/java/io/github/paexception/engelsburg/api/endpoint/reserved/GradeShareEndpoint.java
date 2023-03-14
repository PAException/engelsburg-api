/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.GradeShareController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeShareDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeShareRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeShareRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradeSharesDTO;
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
 * RestController to handle gradeShares of grades.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/grade/share")
public class GradeShareEndpoint {

	private final GradeShareController gradeShareController;

	/**
	 * Create new gradeShare.
	 *
	 * @see GradeShareController#create(CreateGradeShareRequestDTO, UserDTO)
	 */
	@AuthScope("grade.share.write.self")
	@PostMapping
	@Response(GradeShareDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to get that subject")
	public Object createGradeShare(@RequestBody @Valid CreateGradeShareRequestDTO dto, UserDTO userDTO) {
		return this.gradeShareController.create(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update an existing gradeShare.
	 *
	 * @see GradeShareController#update(UpdateGradeShareRequestDTO, UserDTO)
	 */
	@AuthScope("grade.share.write.self")
	@PatchMapping
	@Response(GradeShareDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade_share")
	public Object updateGradeShare(@RequestBody @Valid UpdateGradeShareRequestDTO dto, UserDTO userDTO) {
		return this.gradeShareController.update(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get a gradeShare.
	 *
	 * @see GradeShareController#get(int, UserDTO)
	 */
	@AuthScope("grade.share.read.self")
	@GetMapping("/{gradeShareId}")
	@Response(GradeShareDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade_share")
	public Object getGradeShare(@PathVariable @Min(1) @Schema(example = "14") int gradeShareId, UserDTO userDTO) {
		return this.gradeShareController.get(gradeShareId, userDTO).getHttpResponse();
	}

	/**
	 * Get all existing gradeShare.
	 *
	 * @see GradeShareController#getAll(int, UserDTO, SemesterModel)
	 */
	@AuthScope("grade.share.read.self")
	@GetMapping
	@Response(GetGradeSharesDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share", key = "Grade share")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject", key = "Subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to get that subject")
	public Object getAllGradeShares(
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "14") int subjectId,
			UserDTO userDTO, SemesterModel semester) {
		return this.gradeShareController.getAll(subjectId, userDTO, semester).getHttpResponse();
	}

	/**
	 * Delete a gradeShare.
	 *
	 * @see GradeShareController#delete(int, UserDTO)
	 */
	@AuthScope("grade.share.delete.self")
	@DeleteMapping("/{gradeShareId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "grade_share")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "grade_share")
	@ErrorResponse(status = 424, messageKey = "FAILED_DEPENDENCY", extra = "grade_share", description = "GradeShare cannot be deleted because another entity is depending on it")
	public Object deleteGradeShare(@PathVariable @Schema(example = "14") int gradeShareId, UserDTO userDTO) {
		return this.gradeShareController.delete(gradeShareId, userDTO).getHttpResponse();
	}
}
