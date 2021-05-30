package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.GradeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<GradeModel, Integer> {

	void deleteAllByUserId(UUID userId);

	Object findAllByUserId(UUID userId);

}
