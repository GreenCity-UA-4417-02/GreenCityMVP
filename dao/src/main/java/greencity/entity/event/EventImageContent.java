package greencity.entity.event;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "event_images_content")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventImageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    private String contentType;
}