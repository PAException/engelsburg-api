/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.shared;

import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;

/**
 * Controller to handle all solar system actions.
 */
@Component
public class SolarSystemController {

	private SolarSystemDTO dto;

	/**
	 * Retrieve the current status of the solar system.
	 *
	 * @return the status
	 */
	public Result<SolarSystemDTO> info() {
		return this.dto != null ? Result.of(this.dto) : Result.of(Error.NOT_FOUND, "solar_system");
	}

	/**
	 * Update current status of the solar system.
	 *
	 * @param date         to update
	 * @param energy       to update
	 * @param co2avoidance to update
	 * @param payment      to update
	 */
	public void update(String date, String energy, String co2avoidance, String payment) {
		if (this.dto == null) this.dto = new SolarSystemDTO();

		this.dto.updateData(date, energy, co2avoidance, payment);
	}

	/**
	 * Update text of solar system data.
	 *
	 * @param text to update
	 */
	public void updateText(String text) {
		this.dto.setText(text);
	}
}
