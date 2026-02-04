package greencity.mapping;

import org.modelmapper.AbstractConverter;
import org.modelmapper.spi.MappingContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MapperTestUtils {
    private MapperTestUtils() {
    }

    public static <S, D> D convert(AbstractConverter<S, D> mapper, S source) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        MappingContext context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(source);
        return (D) mapper.convert(context);
    }

    public static <S, D> D convertNull(AbstractConverter<S, D> mapper) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        MappingContext context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);
        return (D) mapper.convert(context);
    }
}