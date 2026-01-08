package greencity.controller;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {
    private MockMvc mockMvc;

    @Mock
    LanguageService languageService;

    @InjectMocks
    LanguageController languageController;

    private static final String languageLink = "/language";

    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .build();

    }


}
