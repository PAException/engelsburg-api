package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for solar system information.
 */
@RestController
public class SolarSystemEndpoint {

	private final SolarSystemController solarSystemController;

	public SolarSystemEndpoint(
			SolarSystemController solarSystemController) {
		this.solarSystemController = solarSystemController;
	}

	/**
	 * Get current status of solar system.
	 *
	 * @see SolarSystemController#info()
	 */
	@GetMapping("/solar_system")
	public Object solarSystem() {
		return this.solarSystemController.info().getHttpResponse();
	}

}
