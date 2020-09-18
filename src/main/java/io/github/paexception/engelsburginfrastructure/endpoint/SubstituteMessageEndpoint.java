package io.github.paexception.engelsburginfrastructure.endpoint;

import io.github.paexception.engelsburginfrastructure.controller.SubstituteMessageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubstituteMessageEndpoint {

	@Autowired private SubstituteMessageController substituteMessageController;

}
