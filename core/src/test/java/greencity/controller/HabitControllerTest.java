package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HabitControllerTest {
    private static final String HABIT_CONTROLLER_LINK = "/habit";
    private MockMvc mockMvc;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private final Principal principal = getPrincipal();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(habitController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    void getHabitByIdTest_isOk() throws Exception {
        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/{id}", 1)
            .param("lang", "en"))
            .andExpect(status().isOk());

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getAllHabitsForCurrentUser_isOk() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK)
            .param("page", "1")
            .principal(principal)
            .param("lang", "en"))
            .andExpect(status().isOk());

        verify(habitService).getAllHabitsByLanguageCode(eq(userVO), any(Pageable.class), eq("en"));
    }

    @Test
    void getShoppingListItemsForHabit_isOk() throws Exception {
        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/{id}/shopping-list", 3))
            .andExpect(status().isOk());

        verify(habitService).getShoppingListForHabit(3L, "en");
    }

    @Test
    void getHabitsByTagAndLang_isOk() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/tags/search")
            .param("tags", tags.toArray(String[]::new))
            .param("lang", "en"))
            .andExpect(status().isOk());

        verify(habitService).getAllByTagsAndLanguageCode(any(Pageable.class), eq(tags), eq("en"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"tags", "complexities"})
    void getHabitsByDifferentParam_paramEmpty_badRequest(String paramName) throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/search")
            .param(paramName, "")
            .param("lang", "en")
            .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getHabitsByDifferentParam_tags_isOk() throws Exception {
        List<String> tags = List.of("tag1", "tag2");

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/search")
            .param("tags", tags.toArray(String[]::new))
            .param("lang", "en")
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(
            eq(userVO),
            any(Pageable.class),
            eq(Optional.of(tags)),
            eq(Optional.empty()),
            eq(Optional.empty()),
            eq("en"));
    }

    @Test
    void getHabitsByDifferentParam_isCustomHabit_isOk() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/search?isCustomHabit=true")
            .param("lang", "en")
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(
            eq(userVO),
            any(Pageable.class),
            eq(Optional.empty()),
            eq(Optional.of(true)),
            eq(Optional.empty()),
            eq("en"));
    }

    @Test
    void getHabitsByDifferentParam_complexities_isOk() throws Exception {
        List<Integer> complexities = List.of(1, 3);

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/search")
            .param("complexities", complexities.stream().map(String::valueOf).toArray(String[]::new))
            .param("lang", "en")
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(
            eq(userVO),
            any(Pageable.class),
            eq(Optional.empty()),
            eq(Optional.empty()),
            eq(Optional.of(complexities)),
            eq("en"));
    }

    @Test
    void getAllHabitsTags_isOk() throws Exception {
        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/tags"))
            .andExpect(status().isOk());

        verify(tagsService).findAllHabitsTags("en");
    }

    @Test
    void addCustomHabit_isCreated() throws Exception {
        String json = """
            {
              "complexity": 2,
              "tagIds": [1, 2]
            }
            """;

        MockMultipartFile requestPart = new MockMultipartFile(
            "request",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            json.getBytes());

        MockMultipartFile imagePart = new MockMultipartFile(
            "image",
            "habit.png",
            MediaType.IMAGE_PNG_VALUE,
            "image".getBytes());

        mockMvc.perform(multipart(HABIT_CONTROLLER_LINK + "/custom")
            .file(requestPart)
            .file(imagePart)
            .principal(principal)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddCustomHabitDtoRequest dtoRequest = mapper.readValue(json, AddCustomHabitDtoRequest.class);

        verify(habitService).addCustomHabit(
            eq(dtoRequest),
            any(MultipartFile.class),
            eq(principal.getName()));
    }

    @Test
    void getFriendsAssignedToHabitProfilePictures_isOk() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(HABIT_CONTROLLER_LINK + "/{habitId}/friends/profile-pictures", 4)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitService).getFriendsAssignedToHabitProfilePictures(4L, userVO.getId());
    }
}
