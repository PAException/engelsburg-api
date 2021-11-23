package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationDeviceRepository extends JpaRepository<NotificationDeviceModel, Integer> {

	List<NotificationDeviceModel> findAllByUser(UserModel user);

	void deleteByUserAndToken(UserModel user, String token);

	void deleteAllByUser(UserModel user);

	boolean existsByUserAndToken(UserModel user, String token);

}
