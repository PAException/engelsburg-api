package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.RefreshTokenModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Integer> {

	void deleteAllByUser(UserModel user);

	Optional<RefreshTokenModel> findByUser(UserModel user);

	Optional<RefreshTokenModel> findByToken(String refreshToken);

}
