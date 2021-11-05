package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.TaskController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.MarkTaskAsDoneRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateTaskRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for task actions.
 */
@RestController
public class TaskEndpoint {

	private final TaskController taskController;

	public TaskEndpoint(TaskController taskController) {
		this.taskController = taskController;
	}

	/**
	 * Create a new task.
	 *
	 * @see TaskController#createTask(CreateTaskRequestDTO, UserDTO)
	 */
	@AuthScope("task.write.self")
	@PostMapping("/task")
	public Object createTask(@RequestBody @Valid CreateTaskRequestDTO dto, UserDTO userDTO) {
		return this.taskController.createTask(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update a task.
	 *
	 * @see TaskController#updateTask(UpdateTaskRequestDTO, UserDTO)
	 */
	@AuthScope("task.write.self")
	@PatchMapping("/task")
	public Object updateTask(@RequestBody @Valid UpdateTaskRequestDTO dto, UserDTO userDTO) {
		return this.taskController.updateTask(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get tasks by specific parameters.
	 *
	 * @see TaskController#getTasks(boolean, long, UserDTO, Paging)
	 */
	@AuthScope("task.read.self")
	@GetMapping("/task")
	public Object getTasks(@RequestParam(required = false, defaultValue = "false") boolean onlyUndone,
			@RequestParam(required = false, defaultValue = "-1") long date, UserDTO userDTO, Paging paging) {
		return this.taskController.getTasks(onlyUndone, date, userDTO, paging).getHttpResponse();
	}

	/**
	 * Mark a task as done.
	 *
	 * @see TaskController#markAsDone(MarkTaskAsDoneRequestDTO, UserDTO)
	 */
	@AuthScope("task.write.self")
	@PatchMapping("/task/done")
	public Object markTaskAsDone(@RequestBody @Valid MarkTaskAsDoneRequestDTO dto, UserDTO userDTO) {
		return this.taskController.markAsDone(dto, userDTO).getHttpResponse();
	}

	/**
	 * Delete a task by taskId.
	 *
	 * @see TaskController#deleteTask(int, UserDTO)
	 */
	@AuthScope("task.delete.self")
	@DeleteMapping("/task/{taskId}")
	public Object deleteTask(@PathVariable int taskId, UserDTO userDTO) {
		return this.taskController.deleteTask(taskId, userDTO).getHttpResponse();
	}

}
