package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.OAuthModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthRepository extends JpaRepository<OAuthModel, Integer> {

	Optional<OAuthModel> findByUserIdAndService(UUID userId, String service);

	void deleteAllByUserId(UUID userId);

	List<OAuthModel> findAllByUserId(UUID userId);

}
