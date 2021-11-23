package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableModel, Integer> {

	void deleteAllByUser(UserModel user);

	List<TimetableModel> findAllByUser(UserModel user);

	Optional<TimetableModel> findByUserAndDayAndLesson(UserModel user, int day, int lesson);

	List<TimetableModel> findAllByDayAndLessonAndClassNameAndTeacher(int day, int lesson, String className,
			String teacher);

	List<TimetableModel> findAllByDayAndLessonAndClassName(int day, int lesson, String className);

	List<TimetableModel> findAllByDayAndLessonAndTeacher(int day, int lesson, String teacher);

	Stream<TimetableModel> findAllByUserAndDayAndLesson(UserModel user, int day, int lesson);

	Stream<TimetableModel> findAllByUserAndDay(UserModel user, int day);

	Stream<TimetableModel> findAllByUserAndLesson(UserModel user, int lesson);

}
