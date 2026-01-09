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
    LanguageController languageController;

    @Mock
    LanguageService languageService;

    private static final String LANGUAGE_LINK = "/language";

    @BeforeEach
    void setUp(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAllLanguageCodes_returnsStatusOk() throws Exception {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));

        mockMvc.perform(get(LANGUAGE_LINK))
                .andExpect(status().isOk());

        verify(languageService).findAllLanguageCodes();
    }

    @Test
    void getAllLanguageCodes_returnsJsonContentType() throws Exception {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));

        mockMvc.perform(get(LANGUAGE_LINK))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(languageService).findAllLanguageCodes();
    }

    @Test
    void getAllLanguageCodes_returnsExpectedLanguages() throws Exception {
        String[] expectedLanguages = {"en", "ua", "fr"};
        List<String> actualLanguages = List.of("en", "ua", "fr");
        when(languageService.findAllLanguageCodes()).thenReturn(actualLanguages);

        mockMvc.perform(get(LANGUAGE_LINK))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.containsInAnyOrder(expectedLanguages)));

        verify(languageService).findAllLanguageCodes();
    }

    @Test
    void getAllLanguageCodes_returnsEmptyList() throws Exception {
        List<String> emptyListOfLanguages = List.of();
        when(languageService.findAllLanguageCodes()).thenReturn(emptyListOfLanguages);

        mockMvc.perform(get(LANGUAGE_LINK))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.empty()));

        verify(languageService).findAllLanguageCodes();
    }
}
