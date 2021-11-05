package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.TaskModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.TaskRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TaskDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateTaskRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.MarkTaskAsDoneRequestDTO;
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
public class TaskController extends AbstractPageable implements UserDataHandler {

	private final TaskRepository taskRepository;

	public TaskController(TaskRepository taskRepository) {
		super(1, 50);
		this.taskRepository = taskRepository;
	}

	/**
	 * Create a new task.
	 *
	 * @param dto     with task information
	 * @param userDTO user information
	 * @return created task
	 */
	public Result<TaskDTO> createTask(CreateTaskRequestDTO dto, UserDTO userDTO) {
		return Result.of(this.taskRepository.save(
				new TaskModel(
						-1,
						userDTO.user,
						dto.getTitle(),
						dto.getCreated() < 0 ? System.currentTimeMillis() : dto.getCreated(),
						dto.getDue(),
						dto.getSubject(),
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
		if (dto.getTaskId() < 0) return Result.of(Error.INVALID_PARAM, NAME_KEY);
		Optional<TaskModel> optionalTask = this.taskRepository.findById(dto.getTaskId());
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		if (!optionalTask.get().getUser().is(userDTO)) Result.of(Error.FORBIDDEN, NAME_KEY);

		TaskModel task = optionalTask.get();
		if (dto.getTitle() != null && !dto.getTitle().isBlank()) task.setTitle(dto.getTitle());
		if (dto.getDue() >= 0) task.setDue(dto.getDue());
		if (dto.getSubject() != null && !dto.getSubject().isBlank()) task.setSubject(dto.getSubject());
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
	 * @param userDTO    user information
	 * @param paging     paging options
	 * @return list of taskDTOs
	 */
	@Transactional
	public Result<GetTasksResponseDTO> getTasks(boolean onlyUndone, long date, UserDTO userDTO, Paging paging) {
		Stream<TaskModel> taskStream;
		if (onlyUndone) {
			if (date < 0) {
				taskStream = this.taskRepository.findAllByUserAndCreatedBeforeAndDoneOrderByCreatedDesc(
						userDTO.user,
						System.currentTimeMillis(), false, this.toPage(paging));
			} else {
				taskStream = this.taskRepository.findAllByUserAndCreatedAfterAndDoneOrderByCreatedAsc(
						userDTO.user, date,
						false, this.toPage(paging));
			}
		} else {
			if (date < 0) {
				taskStream = this.taskRepository.findAllByUserAndCreatedBeforeOrderByCreatedDesc(
						userDTO.user,
						System.currentTimeMillis(), this.toPage(paging));
			} else {
				taskStream = this.taskRepository.findAllByUserAndCreatedAfterOrderByCreatedAsc(
						userDTO.user, date,
						this.toPage(paging));
			}
		}

		List<TaskDTO> dtos = taskStream.map(TaskModel::toResponseDTO).collect(Collectors.toList());
		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetTasksResponseDTO(dtos));
	}

	/**
	 * Mark a task as done or undone.
	 *
	 * @param dto     with taskId and value to be set for done (default = true)
	 * @param userDTO user information
	 * @return empty result
	 */
	public Result<?> markAsDone(MarkTaskAsDoneRequestDTO dto, UserDTO userDTO) {
		Optional<TaskModel> optionalTask = this.taskRepository.findById(dto.getTaskId());
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		if (!optionalTask.get().getUser().is(userDTO)) Result.of(Error.FORBIDDEN, NAME_KEY);

		this.taskRepository.save(optionalTask.get().markAsDone(dto.isDone()));

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
		Optional<TaskModel> optionalTask = this.taskRepository.findById(taskId);
		if (optionalTask.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		TaskModel task = optionalTask.get();
		if (!task.getUser().is(userDTO)) return Result.of(Error.FORBIDDEN, NAME_KEY);
		else {
			this.taskRepository.delete(task);
			return Result.empty();
		}
	}


	@Override
	public void deleteUserData(UserModel user) {
		this.taskRepository.deleteAllByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.taskRepository.findAllByUser(user));
	}

}
