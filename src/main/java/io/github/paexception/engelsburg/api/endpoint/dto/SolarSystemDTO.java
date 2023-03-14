/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolarSystemDTO {

	@Schema(example = "24.02.2022")
	private String date;
	@Schema(example = "81.563,60 kWh")
	private String energy;
	@Schema(example = "57.094,52 kg")
	private String co2avoidance;
	@Schema(example = "38.130,98")
	private String payment;
	@Schema(example = "<p>Some example text...")
	private String text;

	public void updateData(String date, String energy, String co2avoidance, String payment) {
		this.date = date;
		this.energy = energy;
		this.co2avoidance = co2avoidance;
		this.payment = payment;
	}

}
