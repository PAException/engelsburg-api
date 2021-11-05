package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.GradeModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface GradeRepository extends JpaRepository<GradeModel, Integer> {

	void deleteAllByUser(UserModel user);

	List<GradeModel> findAllByUser(UserModel user);

	Stream<GradeModel> findAllByUserAndSubject(UserModel user, String subject);

}
