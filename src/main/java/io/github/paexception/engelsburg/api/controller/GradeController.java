package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Controller for timetable
 */
@Component
public class GradeController implements UserDataHandler {

	@Autowired
	private GradeRepository gradeRepository;

	@Override
	public void deleteUserData(UUID userId) {
		this.gradeRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.gradeRepository.findAllByUserId(userId));
	}

}
