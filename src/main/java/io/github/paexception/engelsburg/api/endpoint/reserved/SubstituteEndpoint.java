/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for substitute actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/substitute")
@Tag(name = "substitutes")
public class SubstituteEndpoint {

	private final SubstituteController substituteController;

	/**
	 * Get all substitutes by filter.
	 *
	 * @see SubstituteController#getSubstitutes(long, int, String, String, String, UserDTO)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping
	@Response(GetSubstitutesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "substitute")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "substitute")
	public Object getSubstitutesByFilter(
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "1645722287599") long date,
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "5") int lesson,
			@RequestParam(required = false) @Schema(example = "10c") String className,
			@RequestParam(required = false) @Schema(example = "BSU") String substituteTeacher,
			@RequestParam(required = false) @Schema(example = "GAR") String teacher,
			UserDTO userDTO) {
		return this.substituteController.getSubstitutes(
				date, lesson, className, substituteTeacher, teacher, userDTO).getHttpResponse();
	}
}
