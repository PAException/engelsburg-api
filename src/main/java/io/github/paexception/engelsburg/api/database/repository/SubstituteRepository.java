package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubstituteRepository extends JpaRepository<SubstituteModel, Integer> {

	static String likeClassName(String className) {
		if (className.length() == 2) return className.charAt(0) + "%" + className.charAt(1) + "%";
		else return className.substring(0, 2) + "%" + className.charAt(2) + "%";
	}

	List<SubstituteModel> findAllByDate(Date date);

	List<SubstituteModel> findAllByDateAndClassName(Date date, String className);

	List<SubstituteModel> findAllByDateAndTeacher(Date date, String teacher);

	List<SubstituteModel> findAllByDateAndTeacherAndLesson(Date date, String teacher, int lesson);

	List<SubstituteModel> findAllByDateAndTeacherAndClassName(Date date, String teacher, String className);

	List<SubstituteModel> findAllByDateAndTeacherAndLessonAndClassName(Date date, String teacher, int lesson,
			String className);

	List<SubstituteModel> findAllByDateAndSubstituteTeacher(Date date, String substituteTeacher);

	List<SubstituteModel> findAllByDateGreaterThanEqual(Date date);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndClassNameIsLike(Date date, String className);

	Optional<SubstituteModel> findByDateAndLessonAndTeacher(Date date, int lesson, String teacher);

	Optional<SubstituteModel> findByDateAndLessonAndClassNameIsLike(Date date, int lesson, String className);

	Optional<SubstituteModel> findByDateAndLessonAndSubject(Date date, int lesson, String subject);

	List<SubstituteModel> findAllByDateLessThanEqual(Date date);
}
