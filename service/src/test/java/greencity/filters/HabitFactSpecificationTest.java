package greencity.filters;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.HabitFactTranslation_;
import greencity.entity.HabitFact_;
import greencity.entity.Habit_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static greencity.entity.HabitFactTranslation_.content;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {

    @Mock
    private Root<HabitFact> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Root<HabitFactTranslation> translationRoot;

    @Mock
    private Join<HabitFact, Habit> habitJoin;

    @Mock
    private Predicate basePredicate;

    @Mock
    private Predicate combinedPredicate;

    @Mock
    private Predicate idPredicate;

    @Mock
    private Predicate habitIdPredicate;

    @Mock
    private Predicate likePredicate;

    @Mock
    private Predicate translationEqPredicate;

    @Mock
    private Predicate translationInnerAndPredicate;

    @Mock
    private Path<Long> idPath;

    @Mock
    private Path<Long> habitIdPath;

    @Mock
    private Path<String> contentPath;

    @Mock
    private Path<Long> rootIdPath;

    @Mock
    private Path<Long> translationHabitFactIdPath;

    @Test
    void toPredicate_shouldCreatePredicateForId() {
        SearchCriteria criteria = SearchCriteria.builder()
                .type("id")
                .key("id")
                .value(1L)
                .build();

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate);
        when(root.get("id")).thenReturn((Path) idPath);
        when(criteriaBuilder.equal(idPath, 1L)).thenReturn(idPredicate);
        when(criteriaBuilder.and(basePredicate, idPredicate)).thenReturn(combinedPredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(criteria));

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(combinedPredicate, result);
        verify(root).get("id");
        verify(criteriaBuilder).equal(idPath, 1L);
        verify(criteriaBuilder).and(basePredicate, idPredicate);
    }

    @Test
    void toPredicate_shouldCreatePredicateForHabitId_withJoin() {
        SearchCriteria criteria = SearchCriteria.builder()
                .type("habitId")
                .value(2L)
                .build();

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn((Path) habitIdPath);
        when(criteriaBuilder.equal(habitIdPath, 2L)).thenReturn(habitIdPredicate);
        when(criteriaBuilder.and(basePredicate, habitIdPredicate)).thenReturn(combinedPredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(criteria));

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(combinedPredicate, result);
        verify(root).join(HabitFact_.habit);
        verify(habitJoin).get(Habit_.id);
        verify(criteriaBuilder).equal(habitIdPath, 2L);
        verify(criteriaBuilder).and(basePredicate, habitIdPredicate);
    }

    @Test
    void toPredicate_shouldCreatePredicateForContent_withFromAndLinksToHabitFactId() {
        SearchCriteria criteria = SearchCriteria.builder()
                .type("content")
                .value("abc")
                .build();

        Predicate translationBase = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate, translationBase);

        doReturn(translationRoot).when(criteriaQuery).from(HabitFactTranslation.class);
        doReturn((Path) contentPath).when(translationRoot).get(eq(content));

        when(root.get(HabitFact_.id)).thenReturn((Path) rootIdPath);
        when(translationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id))
                .thenReturn((Path) translationHabitFactIdPath);

        when(criteriaBuilder.like(contentPath, "%abc%")).thenReturn(likePredicate);
        when(criteriaBuilder.equal(translationHabitFactIdPath, rootIdPath)).thenReturn(translationEqPredicate);
        when(criteriaBuilder.and(likePredicate, translationEqPredicate)).thenReturn(translationInnerAndPredicate);
        when(criteriaBuilder.and(basePredicate, translationInnerAndPredicate)).thenReturn(combinedPredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(criteria));

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(combinedPredicate, result);

        verify(criteriaQuery).from(HabitFactTranslation.class);
        verify(translationRoot, times(3)).get(eq(content));
        verify(criteriaBuilder).like(contentPath, "%abc%");
        verify(criteriaBuilder).equal(translationHabitFactIdPath, rootIdPath);
        verify(criteriaBuilder).and(likePredicate, translationEqPredicate);
        verify(criteriaBuilder).and(basePredicate, translationInnerAndPredicate);
    }

    @Test
    void toPredicate_shouldIgnoreEmptyContent_noLikeNoEq() {
        SearchCriteria criteria = SearchCriteria.builder()
                .type("content")
                .value("   ")
                .build();

        Predicate emptyContentConjunction = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate, emptyContentConjunction);
        doReturn(translationRoot).when(criteriaQuery).from(HabitFactTranslation.class);
        when(criteriaBuilder.and(basePredicate, emptyContentConjunction)).thenReturn(basePredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(criteria));

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(basePredicate, result);

        verify(criteriaQuery).from(HabitFactTranslation.class);
        verify(criteriaBuilder).and(basePredicate, emptyContentConjunction);

        verify(criteriaBuilder, never()).like(any(), anyString());
        verify(criteriaBuilder, never()).equal(any(), any());
    }

    @Test
    void toPredicate_shouldCreateCompoundPredicate_forMultipleCriteria() {
        SearchCriteria byId = SearchCriteria.builder().type("id").key("id").value(1L).build();
        SearchCriteria byHabitId = SearchCriteria.builder().type("habitId").value(2L).build();
        SearchCriteria byContent = SearchCriteria.builder().type("content").value("abc").build();

        Predicate afterId = mock(Predicate.class);
        Predicate afterHabit = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);
        Predicate translationBase = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate, translationBase);

        when(root.get("id")).thenReturn((Path) idPath);
        when(criteriaBuilder.equal(idPath, 1L)).thenReturn(idPredicate);
        when(criteriaBuilder.and(basePredicate, idPredicate)).thenReturn(afterId);

        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn((Path) habitIdPath);
        when(criteriaBuilder.equal(habitIdPath, 2L)).thenReturn(habitIdPredicate);
        when(criteriaBuilder.and(afterId, habitIdPredicate)).thenReturn(afterHabit);

        doReturn(translationRoot).when(criteriaQuery).from(HabitFactTranslation.class);
        doReturn((Path) contentPath).when(translationRoot).get(eq(content));

        when(root.get(HabitFact_.id)).thenReturn((Path) rootIdPath);
        when(translationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id))
                .thenReturn((Path) translationHabitFactIdPath);

        when(criteriaBuilder.like(contentPath, "%abc%")).thenReturn(likePredicate);
        when(criteriaBuilder.equal(translationHabitFactIdPath, rootIdPath)).thenReturn(translationEqPredicate);
        when(criteriaBuilder.and(likePredicate, translationEqPredicate)).thenReturn(translationInnerAndPredicate);
        when(criteriaBuilder.and(afterHabit, translationInnerAndPredicate)).thenReturn(finalPredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(byId, byHabitId, byContent));

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(finalPredicate, result);
    }

    @Test
    void toPredicate_contentNull_shouldThrowNullPointerException() {
        SearchCriteria criteria = SearchCriteria.builder()
                .type("content")
                .value(null)
                .build();

        when(criteriaBuilder.conjunction()).thenReturn(basePredicate);

        HabitFactSpecification specification = new HabitFactSpecification(List.of(criteria));

        assertThrows(NullPointerException.class,
                () -> specification.toPredicate(root, criteriaQuery, criteriaBuilder));
    }
}