package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.repository.GradeRepository;
import io.github.paexception.engelsburg.api.util.Result;
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
	public Result<?> getUserData(UUID userId) {
		return Result.of(this.gradeRepository.findAllByUserId(userId));
	}

}
