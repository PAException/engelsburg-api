package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.TimetableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for timetable actions
 */
@RestController
public class TimetableEndpoint {

	@Autowired private TimetableController timetableController;

}
