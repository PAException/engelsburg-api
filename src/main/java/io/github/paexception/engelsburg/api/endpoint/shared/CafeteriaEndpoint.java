package io.github.paexception.engelsburg.api.endpoint.shared;

import io.github.paexception.engelsburg.api.controller.shared.CafeteriaController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for cafeteria actions.
 */
@RestController
public class CafeteriaEndpoint {

	private final CafeteriaController cafeteriaController;

	public CafeteriaEndpoint(CafeteriaController cafeteriaController) {
		this.cafeteriaController = cafeteriaController;
	}

	/**
	 * Get cafeteria information.
	 *
	 * @return cafeteria info
	 */
	@GetMapping("/cafeteria")
	public Object getCafeteriaInformation() {
		return this.cafeteriaController.getInfo().getHttpResponse();
	}

}
