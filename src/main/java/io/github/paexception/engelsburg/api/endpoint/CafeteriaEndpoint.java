package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.CafeteriaController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for cafeteria actions.
 */
@RestController
public class CafeteriaEndpoint {

	@Autowired
	private CafeteriaController cafeteriaController;

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
