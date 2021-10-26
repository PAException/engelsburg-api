package io.github.paexception.engelsburg.api.endpoint.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteMessageController;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for substitute messages actions.
 */
@Validated
@RestController
public class SubstituteMessageEndpoint {

	@Autowired
	private SubstituteMessageController substituteMessageController;

	/**
	 * Get all substitute messages since date.
	 *
	 * @param date can't be in the past
	 * @return all found substitute messages
	 */
	@AuthScope("substitute.message.read.current")
	@GetMapping("/substitute/message")
	public Object getAllSubstituteMessages(@RequestParam(required = false, defaultValue = "-1") long date, DecodedJWT jwt) {
		return this.substituteMessageController.getAllSubstituteMessages(date, jwt).getHttpResponse();
	}

}
