/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.database.repository.TimetableRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTimetableEntriesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.Timetable.NAME_KEY;

/**
 * Controller for timetable.
 */
@Component
@AllArgsConstructor
public class TimetableController {

	private final TimetableRepository timetableRepository;
	private final SubjectController subjectController;

	/**
	 * Set a new or existing timetable entry.
	 *
	 * @param dto      with entry information
	 * @param userDTO  user information
	 * @param semester semester of user
	 * @return empty result or error
	 */
	public Result<?> setTimetableEntry(TimetableDTO dto, UserDTO userDTO, SemesterModel semester) {
		//Check if subject exists and can be accessed
		Result<SubjectModel> result = this.subjectController.getSubjectRaw(dto.getSubjectId(), userDTO);
		if (result.isErrorPresent()) return result;

		//Get optional timetable of user
		SubjectModel subject = result.getResult();
		Optional<TimetableModel> optionalTimetable = this.timetableRepository
				.findBySemesterAndDayAndLesson(semester, dto.getDay(), dto.getLesson());

		//If timetable present update values, otherwise create new template of timetable with user, day and lesson
		TimetableModel timetable = optionalTimetable
				.orElse(TimetableModel.template(semester, dto.getDay(), dto.getLesson()));
		timetable.setTeacher(dto.getTeacher());
		timetable.setClassName(dto.getClassName());
		timetable.setRoom(dto.getRoom());
		timetable.setSubject(subject);

		//Save timetable and return empty result
		this.timetableRepository.save(timetable);
		return Result.empty();
	}

	/**
	 * Copy the timetable from semester to semester.
	 * Usually called by {@link SemesterController#createSemester(CreateSemesterRequestDTO, UserDTO)}
	 *
	 * @param old      semester to copy from
	 * @param copyTo   semester to copy to
	 * @param subjects a map of subjects with old as key and new as value to replace subjects in timetable entry
	 */
	public void copyTimetable(SemesterModel old, SemesterModel copyTo, Map<SubjectModel, SubjectModel> subjects) {
		//Iterate through all timetable entries of semester to copy from
		for (TimetableModel entry : this.timetableRepository.findAllBySemester(old)) {
			//Change to new entry, update semester and subject
			entry.setTimetableId(-1);
			entry.setSemester(copyTo);
			entry.setSubject(subjects.get(entry.getSubject()));

			//Copy timetable
			this.timetableRepository.save(entry);
		}
	}

	/**
	 * Get timetable entries.
	 * <p>
	 * All, by day or lesson or by day and lesson
	 * </p>
	 *
	 * @param day      filter by day
	 * @param lesson   filter by lesson
	 * @param semester semester information
	 * @return list of timetable entries
	 */
	@Transactional
	public Result<GetTimetableEntriesResponseDTO> getTimetableEntries(int day, int lesson, SemesterModel semester) {
		//Get all timetable dtos by optional day and optional lesson
		List<TimetableDTO> dtos = this.streamTimetable(day, lesson, semester)
				.map(TimetableModel::toResponseDTO).collect(Collectors.toList());

		//Return dtos if not found return error
		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetTimetableEntriesResponseDTO(dtos));
	}

	/**
	 * Get a stream of timetable models of a user by optional day and lesson.
	 *
	 * @param day      (optional)
	 * @param lesson   (optional)
	 * @param semester to get timetable by
	 * @return stream of timetable
	 */
	public Stream<TimetableModel> streamTimetable(int day, int lesson, SemesterModel semester) {
		if (day >= 0 && lesson >= 0)
			return this.timetableRepository.findAllBySemesterAndDayAndLesson(semester, day, lesson);
		else if (day >= 0)
			return this.timetableRepository.findAllBySemesterAndDay(semester, day);
		else if (lesson >= 0)
			return this.timetableRepository.findAllBySemesterAndLesson(semester, lesson);
		else
			return this.timetableRepository.findAllBySemester(semester).stream();
	}

	/**
	 * Delete a new timetable entry.
	 *
	 * @param day      of entry
	 * @param lesson   of entry
	 * @param semester semester information
	 * @return empty result or error
	 */
	@Transactional
	public Result<?> deleteTimetableEntry(int day, int lesson, SemesterModel semester) {
		//Get timetable of user by optional day and optional lesson if not found return error
		List<TimetableModel> timetable = this.streamTimetable(day, lesson, semester)
				.collect(Collectors.toList());
		if (timetable.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//If present delete timetable and return empty result
		this.timetableRepository.deleteAll(timetable);
		return Result.empty();
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
	public List<TimetableModel> getAllByWeekDayAndLessonAndTeacherOrClassName(int day, int lesson, String teacher,
			String className) {
		if (teacher != null && className != null)
			return this.timetableRepository
					.findAllByDayAndLessonAndClassNameAndTeacher(day, lesson, className, teacher);
		else if (teacher != null)
			return this.timetableRepository.findAllByDayAndLessonAndTeacher(day, lesson, teacher);
		else if (className != null)
			return this.timetableRepository.findAllByDayAndLessonAndClassName(day, lesson, className);

		return Collections.emptyList();
	}
}
