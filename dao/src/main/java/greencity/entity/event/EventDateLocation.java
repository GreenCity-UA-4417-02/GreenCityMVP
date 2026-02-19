package greencity.entity.event;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "events_dates_locations")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventDateLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    @NonNull
    @FutureOrPresent(message = "Event date cannot be in the past")
    private LocalDate date;

    @Column
    @Nullable
    private LocalTime startTime;

    @Column
    @Nullable
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isAllDay = false;

    @Embedded
    @Nullable
    private Address address;

    @Column
    @Nullable
    private String onlineLink;

    public boolean isTimeValid() {
        if (date.isEqual(LocalDate.now())) {
            if (startTime != null && startTime.isBefore(LocalTime.now())) {
                return false;
            }
        }
        return true;
    }

    public void setAllDay(boolean allDay) {
        this.isAllDay = allDay;
        if (allDay) {
            this.startTime = LocalTime.of(0, 0);
            this.endTime = LocalTime.of(23, 59);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> effectiveClass =
            o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != effectiveClass) {
            return false;
        }
        EventDateLocation that = (EventDateLocation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}