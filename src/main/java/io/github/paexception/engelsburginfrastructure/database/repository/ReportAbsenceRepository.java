package io.github.paexception.engelsburginfrastructure.database.repository;

import io.github.paexception.engelsburginfrastructure.database.model.ReportAbsenceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportAbsenceRepository extends JpaRepository<ReportAbsenceModel, Integer> {

    List<ReportAbsenceModel> findAllByTeacher(String teacher);

    List<ReportAbsenceModel> findAllByStudent(String student);

    List<ReportAbsenceModel> findAllByTeacherAndStudent(String teacher, String student);

}
