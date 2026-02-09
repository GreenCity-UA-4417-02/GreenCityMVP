package greencity.entity.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "initiative_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiativeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;
}