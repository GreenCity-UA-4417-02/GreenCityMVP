package greencity.dto.event;

import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Builder
public record EventImageContentDto(
        UUID id,
        byte[] imageData,
        String contentType,
        String link
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventImageContentDto debt = (EventImageContentDto) o;
        return Objects.equals(id, debt.id) &&
               Arrays.equals(imageData, debt.imageData) &&
               Objects.equals(contentType, debt.contentType) &&
               Objects.equals(link, debt.link);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, contentType, link);
        result = 31 * result + Arrays.hashCode(imageData);
        return result;
    }

    @Override
    public String toString() {
        return "EventImageContentDto{" +
               "id=" + id +
               ", imageData=" + Arrays.toString(imageData) +
               ", contentType='" + contentType + '\'' +
               ", link='" + link + '\'' +
               '}';
    }
}