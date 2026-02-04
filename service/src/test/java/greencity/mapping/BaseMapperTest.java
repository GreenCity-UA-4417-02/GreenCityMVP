package greencity.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;

public abstract class BaseMapperTest {
    protected ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
    }
}