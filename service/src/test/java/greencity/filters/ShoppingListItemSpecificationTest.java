package greencity.filters;

import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static greencity.entity.Translation_.content;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemSpecificationTest {
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CriteriaQuery<?> criteriaQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Root<ShoppingListItem> root;

    @BeforeEach
    void setUp() {
        lenient().when(criteriaQuery.from((Class) ShoppingListItem.class)).thenReturn((Root) root);
    }

    @Test
    void toPredicate_shouldCreatePredicateForId() {
        SearchCriteria criteria = new SearchCriteria("id", "1", "id");
        ShoppingListItemSpecification specification = new ShoppingListItemSpecification(List.of(criteria));

        Predicate conjunction = mock(Predicate.class);
        Predicate predicate = mock(Predicate.class);

        lenient().when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        lenient().when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        lenient().when(criteriaBuilder.and(any(), any())).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(predicate, result);
    }

    @Test
    void toPredicate_shouldCreatePredicateForContentUsingCriteriaQueryFromTranslation() {
        SearchCriteria criteria = new SearchCriteria("content", "Foo", "content");
        ShoppingListItemSpecification specification = new ShoppingListItemSpecification(List.of(criteria));

        Root<ShoppingListItemTranslation> translationRoot = mock(Root.class, Answers.RETURNS_DEEP_STUBS);
        Path<String> contentPath = mock(Path.class);

        Predicate conjunction = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        lenient().when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        lenient().when(criteriaQuery.from((Class) ShoppingListItemTranslation.class))
                .thenReturn((Root) translationRoot);

        lenient().when(translationRoot.get(content)).thenReturn(contentPath);

        lenient().when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        lenient().when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        lenient().when(criteriaBuilder.and(any(), any())).thenReturn(finalPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(finalPredicate, result);
    }

    @Test
    void toPredicate_shouldSkipFilteringWhenContentIsBlank() {
        SearchCriteria criteria = new SearchCriteria("content", "   ", "content");
        ShoppingListItemSpecification specification = new ShoppingListItemSpecification(List.of(criteria));

        Root<ShoppingListItemTranslation> translationRoot = mock(Root.class, Answers.RETURNS_DEEP_STUBS);

        lenient().when(criteriaQuery.from((Class) ShoppingListItemTranslation.class))
                .thenReturn((Root) translationRoot);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertNull(result);
    }

    @Test
    void toPredicate_shouldCreateCompoundPredicateForIdAndContent() {
        SearchCriteria id = new SearchCriteria("id", "2", "id");
        SearchCriteria contentCriteria = new SearchCriteria("content", "Bar", "content");

        ShoppingListItemSpecification specification =
                new ShoppingListItemSpecification(List.of(id, contentCriteria));

        Root<ShoppingListItemTranslation> translationRoot = mock(Root.class, Answers.RETURNS_DEEP_STUBS);
        Path<String> contentPath = mock(Path.class);

        Predicate predicate = mock(Predicate.class);

        lenient().when(criteriaBuilder.conjunction()).thenReturn(predicate);

        lenient().when(criteriaQuery.from((Class) ShoppingListItemTranslation.class))
                .thenReturn((Root) translationRoot);

        lenient().when(translationRoot.get(content)).thenReturn(contentPath);

        lenient().when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        lenient().when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        lenient().when(criteriaBuilder.and(any(), any())).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(predicate, result);
    }

    @Test
    void toPredicate_shouldIgnoreUnknownType() {
        SearchCriteria unknown = new SearchCriteria("whatever", "x", "unknown");
        ShoppingListItemSpecification specification = new ShoppingListItemSpecification(List.of(unknown));

        Predicate conjunction = mock(Predicate.class);
        lenient().when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(conjunction, result);
    }
}