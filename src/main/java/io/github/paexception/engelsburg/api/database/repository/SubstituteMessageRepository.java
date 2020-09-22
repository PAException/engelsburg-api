package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.sql.Date;
import java.util.List;

public interface SubstituteMessageRepository extends JpaRepository<SubstituteMessageModel, Integer> {

	void deleteByDate(Date date);

	List<SubstituteMessageModel> findAllByDateGreaterThanEqual(Date date);

	List<SubstituteMessageModel> findAllByDate(Date date);

}
