package greencity.repository;

import greencity.entity.event.InitiativeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitiativeTypeRepository extends JpaRepository<InitiativeType, Long> {
}
