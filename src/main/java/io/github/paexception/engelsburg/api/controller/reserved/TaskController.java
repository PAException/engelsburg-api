/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import io.github.paexception.engelsburg.api.database.model.TaskModel;
import io.github.paexception.engelsburg.api.database.repository.TaskRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TaskDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTasksResponseDTO;
import io.github.paexception.engelsburg.api.spring.paging.AbstractPageable;
import io.github.paexception.engelsburg.api.spring.paging.Paging;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.Task.NAME_KEY;

/**
 * Controller for tasks.
 */
@Component
public class TaskController extends AbstractPageable {

	private final TaskRepository taskRepository;
	private final SubjectController subjectController;

	public TaskController(TaskRepository taskRepository, SubjectController subjectController) {
		super(1, 50);
		this.taskRepository = taskRepository;
		this.subjectController = subjectController;
	}

	/**
	 * Create a new task.
	 *
	 * @param dto      with task information
	 * @param userDTO  user information
	 * @param semester semester of user
	 * @return created task
	 */
	public Result<TaskDTO> createTask(CreateTaskRequestDTO dto, UserDTO userDTO, SemesterModel semester) {
		//Get subject by subjectId if error return
		Result<SubjectModel> result = this.subjectController.getSubjectRaw(dto.getSubjectId(), userDTO);
		if (result.isErrorPresent()) return Result.ret(result);

		//Save new task and return dto
		return Result.of(this.taskRepository.save(
				new TaskModel(
						-1,
						semester,
						result.getResult(), //Subject
						dto.getTitle(),
						dto.getCreated() < 0 ? System.currentTimeMillis() : dto.getCreated(),
						dto.getDue() < 0 ? 0 : dto.getDue(),
						dto.getContent(),
						false
				)
		).toResponseDTO());
	}

	/**
	 * Update a specific task (taskId needed).
	 *
	 * @param dto     with task information
	 * @param userDTO user information
	 * @return updated task
	 */
	public Result<TaskDTO> updateTask(UpdateTaskRequestDTO dto, UserDTO userDTO) {
		//Get task by id if not present return error if not owned by user return error
		Optional<TaskModel> optionalTask = this.taskRepository.findById(dto.getTaskId());
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		if (!userDTO.is(optionalTask.get().getSemester().getUser()) && !userDTO.hasScope("task.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Update values if they were specified
		TaskModel task = optionalTask.get();
		if (dto.getTitle() != null && !dto.getTitle().isBlank()) task.setTitle(dto.getTitle());
		if (dto.getDue() >= 0) task.setDue(dto.getDue());
		if (dto.getSubjectId() >= 0) {
			//Get subject by id if error return
			Result<SubjectModel> result = this.subjectController.getSubjectRaw(dto.getSubjectId(), userDTO);
			if (result.isErrorPresent()) return Result.ret(result);

			task.setSubject(result.getResult());
		}
		if (dto.getContent() != null && !dto.getContent().isBlank()) task.setContent(dto.getContent());

		return Result.of(this.taskRepository.save(task).toResponseDTO());
	}

	/**
	 * Get all tasks.
	 * <p>
	 * Possible params are date after and only done tasks.
	 * </p>
	 *
	 * @param onlyUndone filter by task that are not marked as done
	 * @param date       date to filter by
	 * @param semester   semester information
	 * @param paging     paging options
	 * @return list of taskDTOs
	 */
	@Transactional
	public Result<GetTasksResponseDTO> getTasks(boolean onlyUndone, long date, SemesterModel semester, Paging paging) {
		//Get stream depending on specified parameter onlyUndone and date
		Stream<TaskModel> taskStream;
		if (onlyUndone && date < 0) {
			taskStream = this.taskRepository.findAllBySemesterAndCreatedBeforeAndDoneOrderByCreatedDesc(
					semester, System.currentTimeMillis(), false, this.toPage(paging));
		} else if (onlyUndone) {
			taskStream = this.taskRepository.findAllBySemesterAndCreatedAfterAndDoneOrderByCreatedAsc(
					semester, date, false, this.toPage(paging));
		} else if (date < 0) {
			taskStream = this.taskRepository.findAllBySemesterAndCreatedBeforeOrderByCreatedDesc(
					semester, System.currentTimeMillis(), this.toPage(paging));
		} else {
			taskStream = this.taskRepository.findAllBySemesterAndCreatedAfterOrderByCreatedAsc(
					semester, date, this.toPage(paging));
		}

		//Collect if empty return error otherwise tasks
		List<TaskDTO> dtos = taskStream.map(TaskModel::toResponseDTO).collect(Collectors.toList());
		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetTasksResponseDTO(dtos));
	}

	/**
	 * Mark a task as done or undone.
	 *
	 * @param taskId  of task
	 * @param done    value to be set for done (default = true)
	 * @param userDTO user information
	 * @return empty result
	 */
	public Result<?> markAsDone(int taskId, boolean done, UserDTO userDTO) {
		//Get task by id if not present return error if not owned by user return error
		Optional<TaskModel> optionalTask = this.taskRepository.findById(taskId);
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		if (!userDTO.is(optionalTask.get().getSemester().getUser()) && !userDTO.hasScope("task.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Save and return empty result
		this.taskRepository.save(optionalTask.get().markAsDone(done));
		return Result.empty();
	}

	/**
	 * Delete a task.
	 *
	 * @param taskId  of task to delete
	 * @param userDTO user information
	 * @return empty result
	 */
	@Transactional
	public Result<?> deleteTask(int taskId, UserDTO userDTO) {
		//Get task by id if not present return error if not owned by user return error
		Optional<TaskModel> optionalTask = this.taskRepository.findById(taskId);
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		if (!userDTO.is(optionalTask.get().getSemester().getUser()) && !userDTO.hasScope("task.delete.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Delete task and return empty result
		this.taskRepository.delete(optionalTask.get());
		return Result.empty();
	}
}
