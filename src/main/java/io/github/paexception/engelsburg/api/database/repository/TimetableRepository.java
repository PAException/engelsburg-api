/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableModel, Integer> {

	List<TimetableModel> findAllBySemester(SemesterModel semester);

	Optional<TimetableModel> findBySemesterAndDayAndLesson(SemesterModel semester, int day, int lesson);

	List<TimetableModel> findAllByDayAndLessonAndClassNameAndTeacher(int day, int lesson, String className,
			String teacher);

	List<TimetableModel> findAllByDayAndLessonAndClassName(int day, int lesson, String className);

	List<TimetableModel> findAllByDayAndLessonAndTeacher(int day, int lesson, String teacher);

	Stream<TimetableModel> findAllBySemesterAndDayAndLesson(SemesterModel semester, int day, int lesson);

	Stream<TimetableModel> findAllBySemesterAndDay(SemesterModel semester, int day);

	Stream<TimetableModel> findAllBySemesterAndLesson(SemesterModel semester, int lesson);
}
