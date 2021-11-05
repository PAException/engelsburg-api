package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.TimetableRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.DeleteTimetableEntryRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTimetableEntriesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Timetable.NAME_KEY;

/**
 * Controller for timetable.
 */
@Component
public class TimetableController implements UserDataHandler {

	private final TimetableRepository timetableRepository;

	public TimetableController(TimetableRepository timetableRepository) {
		this.timetableRepository = timetableRepository;
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.timetableRepository.deleteAllByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.timetableRepository.findAllByUser(user));
	}

	/**
	 * Set a new or existing timetable entry.
	 *
	 * @param dto     with entry information
	 * @param userDTO user information
	 * @return empty result or error
	 */
	public Result<?> setTimetableEntry(TimetableDTO dto, UserDTO userDTO) {
		if (dto.getDay() == -1 || dto.getLesson() == -1) return Result.of(Error.INVALID_PARAM, NAME_KEY);
		Optional<TimetableModel> optionalTimetable = this.timetableRepository
				.findByUserAndDayAndLesson(userDTO.user, dto.getDay(), dto.getLesson());
		if (optionalTimetable.isEmpty()) this.timetableRepository.save(new TimetableModel(
				-1,
				userDTO.user,
				dto.getDay(),
				dto.getLesson(),
				dto.getTeacher(),
				dto.getClassName(),
				dto.getRoom(),
				dto.getSubject()
		));
		else {
			TimetableModel timetable = optionalTimetable.get();
			timetable.setTeacher(dto.getTeacher());
			timetable.setClassName(dto.getClassName());
			timetable.setRoom(dto.getRoom());
			timetable.setSubject(dto.getSubject());
			this.timetableRepository.save(timetable);
		}

		return Result.empty();
	}

	/**
	 * Get timetable entries.
	 * <p>
	 * All, by day or lesson or by day and lesson
	 * </p>
	 *
	 * @param day     filter by day
	 * @param lesson  filter by lesson
	 * @param userDTO user information
	 * @return list of timetable entries
	 */
	@Transactional
	public Result<GetTimetableEntriesResponseDTO> getTimetableEntries(int day, int lesson, UserDTO userDTO) {
		List<TimetableDTO> dtos;
		if (day >= 0 && lesson >= 0) {
			dtos = this.timetableRepository.findAllByUserAndDayAndLesson(userDTO.user, day, lesson)
					.map(TimetableModel::toResponseDTO).collect(Collectors.toList());
		} else if (day >= 0) {
			dtos = this.timetableRepository.findAllByUserAndDay(userDTO.user, day)
					.map(TimetableModel::toResponseDTO).collect(Collectors.toList());
		} else if (lesson >= 0) {
			dtos = this.timetableRepository.findAllByUserAndLesson(userDTO.user, lesson)
					.map(TimetableModel::toResponseDTO).collect(Collectors.toList());
		} else {
			dtos = this.timetableRepository.findAllByUser(userDTO.user).stream()
					.map(TimetableModel::toResponseDTO).collect(Collectors.toList());
		}

		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetTimetableEntriesResponseDTO(dtos));
	}

	/**
	 * Delete a new timetable entry.
	 *
	 * @param dto     with day and lesson of entry
	 * @param userDTO user information
	 * @return empty result or error
	 */
	@Transactional
	public Result<?> deleteTimetableEntry(DeleteTimetableEntryRequestDTO dto, UserDTO userDTO) {
		Optional<TimetableModel> optionalTimetable = this.timetableRepository
				.findByUserAndDayAndLesson(userDTO.user, dto.getDay(), dto.getLesson());

		if (optionalTimetable.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else {
			this.timetableRepository.delete(optionalTimetable.get());
			return Result.empty();
		}
	}

	/**
	 * Get all timetable entries for notifications.
	 *
	 * @param day       of timetable entry
	 * @param lesson    of timetable entry
	 * @param teacher   of timetable entry
	 * @param className of timetable entry
	 * @return all entries
	 */
	public List<TimetableModel> getAllByWeekDayAndLessonAndTeacherOrClassName(
			int day, int lesson, String teacher, String className) {
		if (teacher != null && className != null) {
			return this.timetableRepository.findAllByDayAndLessonAndClassNameAndTeacher(day, lesson, className,
					teacher);
		} else if (teacher != null) {
			return this.timetableRepository.findAllByDayAndLessonAndTeacher(day, lesson, teacher);
		} else if (className != null) {
			return this.timetableRepository.findAllByDayAndLessonAndClassName(day, lesson, className);
		}

		return Collections.emptyList();
	}

}
