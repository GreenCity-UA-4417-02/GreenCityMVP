package greencity.entity.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_images_content")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventImageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    private String contentType;
}