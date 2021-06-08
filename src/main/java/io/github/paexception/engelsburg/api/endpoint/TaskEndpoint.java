package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.TaskController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetTasksRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.MarkTaskAsDoneRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateTaskRequestDTO;
import io.github.paexception.engelsburg.api.spring.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for task actions
 */
@RestController
public class TaskEndpoint {

	@Autowired
	private TaskController taskController;

	/**
	 * Create a new task
	 *
	 * @see TaskController#createTask(CreateTaskRequestDTO, DecodedJWT)
	 */
	@AuthScope("task.write.self")
	@PostMapping("/task/create")
	public Object createTask(@RequestBody CreateTaskRequestDTO dto, DecodedJWT jwt) {
		return this.taskController.createTask(dto, jwt).getHttpResponse();
	}

	/**
	 * Update a task
	 *
	 * @see TaskController#updateTask(UpdateTaskRequestDTO, DecodedJWT)
	 */
	@AuthScope("task.write.self")
	@PostMapping("/task/update")
	public Object updateTask(@RequestBody UpdateTaskRequestDTO dto, DecodedJWT jwt) {
		return this.taskController.updateTask(dto, jwt).getHttpResponse();
	}

	/**
	 * Get tasks by specific parameters
	 *
	 * @see TaskController#getTasks(GetTasksRequestDTO, DecodedJWT)
	 */
	@AuthScope("task.read.self")
	@GetMapping("/task")
	public Object getTasks(@RequestBody GetTasksRequestDTO dto, DecodedJWT jwt) {
		return this.taskController.getTasks(dto, jwt).getHttpResponse();
	}

	/**
	 * Mark a task as done
	 *
	 * @see TaskController#markAsDone(MarkTaskAsDoneRequestDTO, DecodedJWT)
	 */
	@AuthScope("task.write.self")
	@PostMapping("/task/done")
	public Object markTaskAsDone(@RequestBody MarkTaskAsDoneRequestDTO dto, DecodedJWT jwt) {
		return this.taskController.markAsDone(dto, jwt).getHttpResponse();
	}

	/**
	 * Delete a task by taskId
	 *
	 * @see TaskController#deleteTask(int, DecodedJWT)
	 */
	@AuthScope("task.delete.self")
	@DeleteMapping("/task/{taskId}")
	public Object deleteTask(@PathVariable int taskId, DecodedJWT jwt) {
		return this.taskController.deleteTask(taskId, jwt).getHttpResponse();
	}

}
