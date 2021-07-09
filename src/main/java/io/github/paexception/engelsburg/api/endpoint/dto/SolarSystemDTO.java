package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolarSystemDTO {

	private String date;
	private String energy;
	private String co2avoidance;
	private String payment;

}
