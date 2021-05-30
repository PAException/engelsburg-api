package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableModel, Integer> {

	void deleteAllByUserId(UUID userId);

	List<TimetableModel> findAllByUserId(UUID userId);

}
