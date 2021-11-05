package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenModel, Integer> {

	Optional<TokenModel> findByUserAndTypeAndToken(UserModel user, String type, String token);

	void deleteByUserAndTypeAndToken(UserModel user, String type, String token);

	void deleteByUser(UserModel user);

	List<TokenModel> findAllByUser(UserModel user);

}
