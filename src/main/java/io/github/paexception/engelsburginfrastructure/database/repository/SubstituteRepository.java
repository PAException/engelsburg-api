package io.github.paexception.engelsburginfrastructure.database.repository;

import io.github.paexception.engelsburginfrastructure.database.model.SubstituteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubstituteRepository extends JpaRepository<SubstituteModel, Integer> {

    Optional<SubstituteModel> findByDateAndLessonContainingAndTeacher(Date date, String lesson, String teacher);

    List<SubstituteModel> findAllByDate(Date date);

    List<SubstituteModel> findAllByDateAndClassName(Date date, String className);

    List<SubstituteModel> findAllByDateAndTeacher(Date date, String teacher);

    List<SubstituteModel> findAllByDateAndTeacherAndLessonContaining(Date date, String teacher, String lesson);

    List<SubstituteModel> findAllByDateAndTeacherAndClassName(Date date, String teacher, String className);

    List<SubstituteModel> findAllByDateAndTeacherAndLessonContainingAndClassName(Date date, String teacher, String lesson, String className);

    List<SubstituteModel> findAllByDateAndSubstituteTeacher(Date date, String substituteTeacher);

}
