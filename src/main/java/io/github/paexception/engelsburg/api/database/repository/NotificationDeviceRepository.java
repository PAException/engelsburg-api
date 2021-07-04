package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationDeviceRepository extends JpaRepository<NotificationDeviceModel, Integer> {

	List<NotificationDeviceModel> findAllByUserId(UUID userId);

	void deleteByUserIdAndToken(UUID userId, String token);

	void deleteAllByUserId(UUID userId);

	boolean existsByUserIdAndToken(UUID userId, String token);

}
