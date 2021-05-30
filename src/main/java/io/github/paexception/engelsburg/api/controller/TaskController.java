package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.repository.TaskRepository;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Controller for timetable
 */
@Component
public class TaskController implements UserDataHandler {

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public void deleteUserData(UUID userId) {
		this.taskRepository.deleteAllByUserId(userId);
	}

	@Override
	public Result<?> getUserData(UUID userId) {
		return Result.of(this.taskRepository.findAllByUserId(userId));
	}

}
