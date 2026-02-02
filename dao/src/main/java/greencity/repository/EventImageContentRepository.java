package greencity.repository;

import greencity.entity.event.EventImageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventImageContentRepository extends JpaRepository<EventImageContent, Long> {
}