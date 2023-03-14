/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for solar system information.
 */
@RestController
@AllArgsConstructor
@Tag(name = "other")
public class SolarSystemEndpoint {

	private final SolarSystemController solarSystemController;

	/**
	 * Get current status of solar system.
	 *
	 * @see SolarSystemController#info()
	 */
	@GetMapping("/solar_system")
	@Response(SolarSystemDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "solar_system")
	public Object solarSystem() {
		return this.solarSystemController.info().getHttpResponse();
	}
}
