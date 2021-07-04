package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

	UserModel findByUserId(UUID userId);

	void deleteByUserId(UUID userId);

	Optional<UserModel> findByEmail(String email);

}
