package greencity.controller;

import greencity.service.LanguageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LanguageControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private LanguageController languageController;

    @Mock
    private LanguageService languageService;

    private static final String LANGUAGE_LINK = "/language";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }

    @Test
    void getAllLanguageCodes_returnsValidResponse() throws Exception {
        List<String> actualLanguages = List.of("en", "ua", "fr");
        String[] expectedLanguages = {"en", "ua", "fr"};

        performAndVerifyLanguageCodes(actualLanguages, expectedLanguages);
    }

    @Test
    void getAllLanguageCodes_returnsEmptyList() throws Exception {
        List<String> emptyListOfLanguages = List.of();

        performAndVerifyLanguageCodes(emptyListOfLanguages, new String[] {});
    }

    private void performAndVerifyLanguageCodes(List<String> mockResponse, String[] expectedJsonArray) throws Exception {
        when(languageService.findAllLanguageCodes()).thenReturn(mockResponse);

        mockMvc.perform(get(LANGUAGE_LINK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.containsInAnyOrder(expectedJsonArray)));

        verify(languageService).findAllLanguageCodes();
    }
}
