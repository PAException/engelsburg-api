package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScopeRepository extends JpaRepository<ScopeModel, Integer> {

	List<ScopeModel> findAllByUserId(UUID userId);

	void deleteAllByUserId(UUID userId);

	boolean existsByUserIdAndScope(UUID userId, String scope);

}
