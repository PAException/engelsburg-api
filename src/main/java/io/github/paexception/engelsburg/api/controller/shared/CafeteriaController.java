/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.shared;

import io.github.paexception.engelsburg.api.endpoint.dto.CafeteriaInformationDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;

@Component
public class CafeteriaController {

	private CafeteriaInformationDTO dto;

	/**
	 * Update the current cafeteria information.
	 *
	 * @param dto to update
	 */
	public void update(CafeteriaInformationDTO dto) {
		this.dto = dto;
	}

	/**
	 * Get cafeteria information.
	 *
	 * @return cafeteria information
	 */
	public Result<CafeteriaInformationDTO> getInfo() {
		return this.dto != null ? Result.of(this.dto) : Result.of(Error.NOT_FOUND, "cafeteria");
	}
}
