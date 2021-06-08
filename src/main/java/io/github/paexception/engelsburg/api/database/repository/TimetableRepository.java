package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableModel, Integer> {

	void deleteAllByUserId(UUID userId);

	List<TimetableModel> findAllByUserId(UUID userId);

	Optional<TimetableModel> findByUserIdAndDayAndLesson(UUID userId, int day, int lesson);

	List<TimetableModel> findAllByDayAndLessonAndClassNameAndTeacher(int day, int lesson, String className, String teacher);

	List<TimetableModel> findAllByDayAndLessonAndClassName(int day, int lesson, String className);

	List<TimetableModel> findAllByDayAndLessonAndTeacher(int day, int lesson, String teacher);

	Stream<TimetableModel> findAllByUserIdAndDayAndLesson(UUID userId, int day, int lesson);

	Stream<TimetableModel> findAllByUserIdAndDay(UUID userId, int day);

	Stream<TimetableModel> findAllByUserIdAndLesson(UUID userId, int lesson);

}
