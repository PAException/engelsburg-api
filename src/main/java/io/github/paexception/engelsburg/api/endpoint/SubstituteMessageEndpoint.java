package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.SubstituteMessageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubstituteMessageEndpoint {

	@Autowired private SubstituteMessageController substituteMessageController;

	@GetMapping("/substitute/message")
	public Object getAllSubstituteMessages(@RequestParam(required = false, defaultValue = "0") long date) {
		return this.substituteMessageController.getAllSubstituteMessages(date).getHttpResponse();
	}

}
