/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SubstituteMessageController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteMessagesResponseDTO;
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
 * RestController for substitute messages actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/substitute/message")
@Tag(name = "substitutes")
public class SubstituteMessageEndpoint {

	private final SubstituteMessageController substituteMessageController;

	/**
	 * Get all substitute messages since date.
	 *
	 * @param date can't be in the past
	 * @return all found substitute messages
	 * @see SubstituteMessageController#getAllSubstituteMessages(long, UserDTO)
	 */
	@AuthScope("substitute.message.read.current")
	@GetMapping
	@Response(GetSubstituteMessagesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "substitute_message")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "substitute_message")
	public Object getAllSubstituteMessages(
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "1645721680045") long date,
			UserDTO userDTO) {
		return this.substituteMessageController.getAllSubstituteMessages(date, userDTO).getHttpResponse();
	}

}
