/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.TaskController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.endpoint.dto.TaskDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTasksResponseDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * RestController for task actions.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/task")
public class TaskEndpoint {

	private final TaskController taskController;

	/**
	 * Create a new task.
	 *
	 * @see TaskController#createTask(CreateTaskRequestDTO, UserDTO, SemesterModel)
	 */
	@AuthScope("task.write.self")
	@PostMapping
	@Response(TaskDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject", key = "Subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to get that subject", key = "Subject")
	public Object createTask(@RequestBody @Valid CreateTaskRequestDTO dto, UserDTO userDTO, SemesterModel semester) {
		return this.taskController.createTask(dto, userDTO, semester).getHttpResponse();
	}

	/**
	 * Update a task.
	 *
	 * @see TaskController#updateTask(UpdateTaskRequestDTO, UserDTO)
	 */
	@AuthScope("task.write.self")
	@PatchMapping
	@Response(TaskDTO.class)
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "subject")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "subject", description = "If the user has not the permission to read the subject")
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "task", key = "Task")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "task", description = "If the user has not the permission to update the task", key = "Task")
	public Object updateTask(@RequestBody @Valid UpdateTaskRequestDTO dto, UserDTO userDTO) {
		return this.taskController.updateTask(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get tasks by specific parameters.
	 *
	 * @see TaskController#getTasks(boolean, long, SemesterModel, Paging)
	 */
	@AuthScope("task.read.self")
	@GetMapping
	@Response(GetTasksResponseDTO.class)
	public Object getTasks(
			@RequestParam(required = false, defaultValue = "false") @Schema(example = "true", description = "Get only tasks which are not marked as done") boolean onlyUndone,
			@RequestParam(required = false, defaultValue = "-1") @Schema(example = "1645537669407", description = "Get only tasks after specified date") long date,
			SemesterModel semester,
			Paging paging) {
		return this.taskController.getTasks(onlyUndone, date, semester, paging).getHttpResponse();
	}

	/**
	 * Mark a task as done.
	 *
	 * @see TaskController#markAsDone(int, boolean, UserDTO)
	 */
	@AuthScope("task.write.self")
	@PatchMapping("/done/{taskId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "task")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "task", description = "If the user has not the permission to update the task")
	public Object markTaskAsDone(
			@PathVariable @Min(1) @Schema(example = "21") int taskId,
			@RequestParam(required = false, defaultValue = "true") @Schema(example = "true", defaultValue = "true") boolean done,
			UserDTO userDTO) {
		return this.taskController.markAsDone(taskId, done, userDTO).getHttpResponse();
	}

	/**
	 * Delete a task by taskId.
	 *
	 * @see TaskController#deleteTask(int, UserDTO)
	 */
	@AuthScope("task.delete.self")
	@DeleteMapping("/{taskId}")
	@Response
	@ErrorResponse(status = 404, messageKey = "NOT_FOUND", extra = "task")
	@ErrorResponse(status = 403, messageKey = "FORBIDDEN", extra = "task", description = "If the user has not the permission to update the task")
	public Object deleteTask(@PathVariable @Min(1) @Schema(example = "21") int taskId, UserDTO userDTO) {
		return this.taskController.deleteTask(taskId, userDTO).getHttpResponse();
	}

}
