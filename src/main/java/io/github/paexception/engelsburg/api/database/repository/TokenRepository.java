package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenModel, Integer> {

	Optional<TokenModel> findByUserIdAndTypeAndToken(UUID userId, String type, String token);

	void deleteByUserIdAndTypeAndToken(UUID userId, String type, String token);

	void deleteByUserId(UUID userId);

	List<TokenModel> findAllByUserId(UUID userId);

}
