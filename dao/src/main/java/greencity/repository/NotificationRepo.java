package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId, Pageable page);

    long countByRecipientUserIdAndReadAtIsNull(Long recipientUserId);

    List<Notification> findAllByrecipientUserIdAndIsDeletedFalse(Long recipientUserId);
}
