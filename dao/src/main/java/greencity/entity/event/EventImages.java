package greencity.entity.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "events_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NonNull
    @NotBlank(message = "Image link is required")
    private String link;

    @Lob
    @Column(name = "data")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private boolean isMain = false;

    public static EventImages createMainImage(String link, byte[] data, Event event) {
        return EventImages.builder()
                .link(link)
                .data(data)
                .event(event)
                .isMain(true)
                .build();
    }

    public static EventImages createAdditionalImage(String link, byte[] data, Event event) {
        return EventImages.builder()
                .link(link)
                .data(data)
                .event(event)
                .isMain(false)
                .build();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EventImages that = (EventImages) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}