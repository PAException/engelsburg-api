package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.database.repository.TimetableRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.DeleteTimetableEntryRequestDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static io.github.paexception.engelsburg.api.util.Constants.Timetable.NAME_KEY;

/**
 * Controller for timetable
 */
@Component
public class TimetableController implements UserDataHandler {

	@Autowired
	private TimetableRepository timetableRepository;

	@Override
	public void deleteUserData(UUID userId) {
		this.timetableRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.timetableRepository.findAllByUserId(userId));
	}

	/**
	 * Set a new timetable entry
	 *
	 * @param dto with entry information
	 * @param jwt to identify user and check permissions
	 * @return empty result or error
	 */
	public Result<?> setTimetableEntry(TimetableDTO dto, DecodedJWT jwt) {
		if (dto.getDay() == -1 || dto.getLesson() == -1) return Result.of(Error.MISSING_PARAM, NAME_KEY);
		UUID userId = UUID.fromString(jwt.getSubject());
		Optional<TimetableModel> optionalTimetable = this.timetableRepository
				.findByUserIdAndDayAndLesson(userId, dto.getDay(), dto.getLesson());
		if (optionalTimetable.isEmpty()) this.timetableRepository.save(new TimetableModel(
				-1,
				userId,
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
	 * Delete a new timetable entry
	 *
	 * @param dto with day and lesson of entry
	 * @param jwt to identify user and check permissions
	 * @return empty result or error
	 */
	public Result<?> deleteTimetableEntry(DeleteTimetableEntryRequestDTO dto, DecodedJWT jwt) {
		return null;//TODO
	}

	/**
	 * Get all timetable entries for notifications
	 *
	 * @return all entries
	 */
	public List<TimetableModel> getAllByWeekDayAndLessonAndTeacherOrClassName(
			int day, int lesson, String teacher, String className) {
		if (teacher != null && className != null) {
			return this.timetableRepository.findAllByDayAndLessonAndClassNameAndTeacher(day, lesson, className, teacher);
		} else if (teacher != null) {
			return this.timetableRepository.findAllByDayAndLessonAndTeacher(day, lesson, teacher);
		} else if (className != null) {
			return this.timetableRepository.findAllByDayAndLessonAndClassName(day, lesson, className);
		}

		return Collections.emptyList();
	}

}
