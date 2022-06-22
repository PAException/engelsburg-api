/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.CafeteriaController;
import io.github.paexception.engelsburg.api.endpoint.dto.CafeteriaInformationDTO;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for cafeteria actions.
 */
@RestController
@AllArgsConstructor
@Tag(name = "other")
public class CafeteriaEndpoint {

	private final CafeteriaController cafeteriaController;

	/**
	 * Get cafeteria information.
	 *
	 * @return cafeteria info
	 */
	@GetMapping("/cafeteria")
	@Response(CafeteriaInformationDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "cafeteria")
	public Object getCafeteriaInformation() {
		return this.cafeteriaController.getInfo().getHttpResponse();
	}
}
