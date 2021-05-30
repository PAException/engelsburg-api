package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public interface SubstituteRepository extends JpaRepository<SubstituteModel, Integer> {

	List<SubstituteModel> findAllByDate(Date date);

	List<SubstituteModel> findAllByDateAndClassName(Date date, String className);

	List<SubstituteModel> findAllByDateAndTeacher(Date date, String teacher);

	List<SubstituteModel> findAllByDateAndTeacherAndLessonContaining(Date date, String teacher, String lesson);

	List<SubstituteModel> findAllByDateAndTeacherAndClassName(Date date, String teacher, String className);

	List<SubstituteModel> findAllByDateAndTeacherAndLessonContainingAndClassName(Date date, String teacher, String lesson, String className);

	List<SubstituteModel> findAllByDateAndSubstituteTeacher(Date date, String substituteTeacher);

	List<SubstituteModel> findAllByDateGreaterThanEqual(Date date);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndClassName(Date date, String className);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndClassNameMatchesRegex(Date date, String classNameRegex);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndTeacher(Date date, String teacher);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndTeacherAndLessonContaining(Date date, String teacher, String lesson);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndTeacherAndClassName(Date date, String teacher, String className);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndTeacherAndLessonContainingAndClassName(Date date, String teacher, String lesson, String className);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndSubstituteTeacher(Date date, String substituteTeacher);

	void deleteAllByDateAndLessonAndTeacher(Date date, String lesson, String teacher);

	void deleteAllByDateAndLessonAndClassNameMatchesRegex(Date date, String lesson, String className);

	void deleteAllByDateAndLessonAndSubject(Date date, String lesson, String subject);

}
