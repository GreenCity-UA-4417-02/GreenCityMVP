package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId, Pageable page);

    Page<Notification> findAllByRecipientUserIdAndOriginOrderByCreatedAtDesc(Long recipientUserId, NotificationOrigin origin, Pageable page);

    long countByRecipientUserIdAndReadAtIsNull(Long recipientUserId);
}
