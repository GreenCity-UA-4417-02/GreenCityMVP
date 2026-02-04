package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.Language;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EcoNewsMappersTest {
    @Test
    void ecoNewsDtoMapper_shouldMapEntityToDto() {
        Language en = Language.builder()
                .id(1L)
                .code(AppConstant.DEFAULT_LANGUAGE_CODE)
                .build();

        Language ua = Language.builder()
                .id(2L)
                .code("ua")
                .build();

        TagTranslation enTranslation = TagTranslation.builder()
                .id(11L)
                .name("Eco")
                .language(en)
                .build();

        TagTranslation uaTranslation = TagTranslation.builder()
                .id(12L)
                .name("Еко")
                .language(ua)
                .build();

        Tag tag = Tag.builder()
                .id(100L)
                .tagTranslations(List.of(enTranslation, uaTranslation))
                .build();

        User author = User.builder()
                .id(50L)
                .name("Nick")
                .build();

        ZonedDateTime creationDate = ZonedDateTime.now();

        EcoNewsComment notDeletedComment = EcoNewsComment.builder()
                .id(1L)
                .deleted(false)
                .build();

        EcoNewsComment deletedComment = EcoNewsComment.builder()
                .id(2L)
                .deleted(true)
                .build();

        EcoNews news = EcoNews.builder()
                .id(10L)
                .author(author)
                .text("Text content")
                .creationDate(creationDate)
                .imagePath("img.png")
                .shortInfo("Short")
                .title("Title")
                .tags(List.of(tag))
                .usersLikedNews(Set.of(author))
                .usersDislikedNews(Set.of())
                .ecoNewsComments(List.of(notDeletedComment, deletedComment))
                .build();

        EcoNewsDtoMapper mapper = new EcoNewsDtoMapper();

        EcoNewsDto dto = mapper.convert(news);

        assertNotNull(dto);

        assertEquals(10L, dto.getId());
        assertEquals("Text content", dto.getContent());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals("img.png", dto.getImagePath());
        assertEquals("Short", dto.getShortInfo());
        assertEquals("Title", dto.getTitle());

        assertNotNull(dto.getAuthor());
        assertEquals(50L, dto.getAuthor().getId());
        assertEquals("Nick", dto.getAuthor().getName());

        assertEquals(1, dto.getLikes());
        assertEquals(0, dto.getDislikes());

        assertNotNull(dto.getTags());
        assertEquals(List.of("Eco"), dto.getTags());

        assertNotNull(dto.getTagsUa());
        assertEquals(List.of("Еко"), dto.getTagsUa());

        assertEquals(1, dto.getCountComments());
    }

    @Test
    void ecoNewsDtoMapper_nullInput_shouldThrowNullPointerException() {
        EcoNewsDtoMapper mapper = new EcoNewsDtoMapper();
        assertThrows(NullPointerException.class, () -> mapper.convert((EcoNews) null));
    }
}