package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.TeacherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherModel, Integer> {

	Optional<TeacherModel> findByAbbreviation(String abbreviation);

}
