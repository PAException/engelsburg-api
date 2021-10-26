package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Integer> {

	void deleteAllByUserId(UUID userId);

	Optional<RefreshTokenModel> findByUserId(UUID userId);

	Optional<RefreshTokenModel> findByToken(String refreshToken);

}
