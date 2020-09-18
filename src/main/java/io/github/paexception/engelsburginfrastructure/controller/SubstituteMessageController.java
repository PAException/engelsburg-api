package io.github.paexception.engelsburginfrastructure.controller;

import io.github.paexception.engelsburginfrastructure.database.repository.SubstituteMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubstituteMessageController {

	@Autowired private SubstituteMessageRepository substituteMessageRepository;

}
