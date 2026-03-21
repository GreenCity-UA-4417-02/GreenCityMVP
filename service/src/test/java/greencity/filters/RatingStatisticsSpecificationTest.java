package greencity.filters;

import greencity.annotations.RatingCalculationEnum;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RatingStatisticsSpecificationTest {

    @Mock
    private Root<RatingStatistics> root;
    @Mock
    private CriteriaQuery<?> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Path<Object> path;
    @Mock
    private Path<Object> userPath;
    @Mock
    private Join<RatingStatistics, User> userJoin;
    @Mock
    private Predicate expectedPredicate;
    @Mock
    private Predicate conjunctionPredicate;
    @Mock
    private Predicate disjunctionPredicate;

    private RatingStatisticsSpecification ratingStatisticsSpecification;

    @BeforeEach
    void setUp() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.disjunction()).thenReturn(disjunctionPredicate);
        when(criteriaBuilder.and(any(), any())).thenReturn(expectedPredicate);
        when(root.get(anyString())).thenReturn(path);
        when(root.join((SingularAttribute<RatingStatistics, User>) any())).thenReturn(userJoin);
        when(userJoin.get((SingularAttribute<User, Object>) any())).thenReturn(userPath);
    }

    @Test
    void toPredicate_withNumericId_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("id")
            .key("id")
            .value(1L)
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));
        
        when(criteriaBuilder.equal(path, 1L)).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).equal(path, 1L);
    }

    @Test
    void toPredicate_withEnumEventName_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("enum")
            .key("ratingCalculationEnum")
            .value("LIKE_COMMENT")
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.or(any(), any())).thenReturn(expectedPredicate);
        when(criteriaBuilder.equal(path, RatingCalculationEnum.LIKE_COMMENT)).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder, atLeastOnce()).or(any(), any());
    }

    @Test
    void toPredicate_withUserId_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("userId")
            .key("user")
            .value("1")
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.equal(userPath, "1")).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).equal(userPath, "1");
    }

    @Test
    void toPredicate_withUserId_NumberFormatException_returnsDisjunction() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("userId")
            .key("user")
            .value("invalid") // Not a number, will cause NumberFormatException if equal fails
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.equal(userPath, "invalid")).thenThrow(new NumberFormatException());

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).disjunction();
    }

    @Test
    void toPredicate_withUserId_EmptyString_returnsConjunction() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("userId")
            .key("user")
            .value(" ") // Empty string causes NumberFormatException and returns conjunction
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.equal(userPath, " ")).thenThrow(new NumberFormatException());

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder, times(2)).conjunction();
    }

    @Test
    void toPredicate_withUserMail_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("userMail")
            .key("user")
            .value("test@email.com")
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.like(any(Expression.class), eq("%test@email.com%"))).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).like(any(Expression.class), eq("%test@email.com%"));
    }

    @Test
    void toPredicate_withDateRange_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("dateRange")
            .key("createDate")
            .value(new String[]{"2023-01-01", "2023-01-02"})
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.between(any(Expression.class), any(java.time.ZonedDateTime.class), any(java.time.ZonedDateTime.class))).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).between(any(Expression.class), any(java.time.ZonedDateTime.class), any(java.time.ZonedDateTime.class));
    }

    @Test
    void toPredicate_withPointsChanged_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("pointsChanged")
            .key("pointsChanged")
            .value(10L)
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.equal(path, 10L)).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).equal(path, 10L);
    }

    @Test
    void toPredicate_withCurrentRating_returnsPredicate() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("currentRating")
            .key("rating")
            .value(50L)
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        when(criteriaBuilder.equal(path, 50L)).thenReturn(expectedPredicate);

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(expectedPredicate, result);
        verify(criteriaBuilder).equal(path, 50L);
    }
    
    @Test
    void toPredicate_withUnknownType_returnsConjunction() {
        SearchCriteria criteria = SearchCriteria.builder()
            .type("unknown")
            .key("unknownKey")
            .value("value")
            .build();
        ratingStatisticsSpecification = new RatingStatisticsSpecification(List.of(criteria));

        Predicate result = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(conjunctionPredicate, result);
    }
}
