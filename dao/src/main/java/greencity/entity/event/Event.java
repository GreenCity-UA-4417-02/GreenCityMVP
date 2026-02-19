package greencity.entity.event;

import greencity.entity.User;
import greencity.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 70)
    @NonNull
    @NotBlank(message = "Event title is required")
    @Size(max = 70, message = "Event title cannot exceed 70 characters")
    private String title;

    @Column
    private String titleImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    private LocalDate creationDate = LocalDate.now();

    @Column(nullable = false, length = 63206)
    @NonNull
    @NotBlank(message = "Event description is required")
    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    private String description;

    @NonNull
    @OrderBy("date ASC")
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 7, message = "Cannot add more than 7 dates")
    private List<EventDateLocation> dates = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 5, message = "Cannot upload more than 5 images")
    private List<EventImages> additionalImages = new ArrayList<>();

    @Column(nullable = false)
    private boolean isOpen = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiative_type_id")
    private InitiativeType initiativeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventGrade> eventGrades = new ArrayList<>();

    public boolean isEventTypeValid() {
        if (type == null)
            return false;

        return switch (type) {
            case OFFLINE -> dates.stream()
                .allMatch(date -> date.getAddress() != null && date.getOnlineLink() == null);
            case ONLINE -> dates.stream()
                .allMatch(date -> date.getOnlineLink() != null && date.getAddress() == null);
            case ONLINE_OFFLINE -> dates.stream()
                .allMatch(date -> date.getAddress() != null && date.getOnlineLink() != null);
        };
    }

    public boolean areFiltersSelected() {
        return initiativeType != null && eventCategory != null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass =
            o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}