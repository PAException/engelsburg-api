/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteKeyHash;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Hash;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
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
	 * @see SubstituteController#getSubstitutes(String, String)
	 */
	@GetMapping
	@Response(GetSubstitutesResponseDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "substitute")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "substitute")
	public Object getSubstitutesByFilter(
			@RequestParam @Schema(example = "<substituteKey>") String substituteKey,
			@RequestParam(required = false) @Schema(example = "10c,9b") String classes,
			@RequestParam(required = false) @Schema(example = "GAR,GRB") String teacher) {
		if (!Environment.SCHOOL_TOKEN.equals(substituteKey))
			return Result.of(Error.FORBIDDEN, "substitute").getHttpResponse();

		return this.substituteController.getSubstitutes(classes, teacher).getHttpResponse();
	}

	/**
	 * Get the hex encoded hash of the substitute key.
	 */
	@GetMapping("/key")
	@Response(GetSubstituteKeyHash.class)
	public Object getSubstituteKeyHash() {
		return Result.of(
				new GetSubstituteKeyHash(Hex.encodeHexString(Hash.sha1(Environment.SCHOOL_TOKEN)))).getHttpResponse();
	}
}
