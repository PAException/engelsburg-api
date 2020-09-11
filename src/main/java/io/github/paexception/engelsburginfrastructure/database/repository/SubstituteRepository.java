package io.github.paexception.engelsburginfrastructure.database.repository;

import io.github.paexception.engelsburginfrastructure.database.model.SubstituteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubstituteRepository extends JpaRepository<SubstituteModel, Integer> {

    List<SubstituteModel> findAllByClassName(String className);

    List<SubstituteModel> findAllByTeacher(String teacher);

    List<SubstituteModel> findAllByLessonAndTeacher(int lesson, String teacher);

    List<SubstituteModel> findAllBySubstituteTeacher(String substituteTeacher);

    List<SubstituteModel> findAllByTeacherAndSubstituteTeacher(String teacher, String substituteTeacher);

    List<SubstituteModel> findAllByTeacherAndClassName(String teacher, String className);

}
