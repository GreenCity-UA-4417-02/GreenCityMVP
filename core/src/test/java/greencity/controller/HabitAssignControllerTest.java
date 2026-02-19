package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings(strictness = Strictness.LENIENT)

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private static final String habitAssignLink = "/habit/assign";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Principal principal = ModelUtils.getPrincipal();
    private final UserVO userVO = ModelUtils.getUserVO();
    private MockMvc mockMvc;
    @Mock
    private HabitAssignService habitAssignService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HabitAssignController habitAssignController;

    @BeforeEach
    void setup() {
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void assignDefault() throws Exception {
        Long habitId = 1L;
        when(habitAssignService.assignDefaultHabitForUser(eq(habitId), eq(userVO)))
            .thenReturn(new HabitAssignManagementDto());

        mockMvc.perform(post(habitAssignLink + "/{habitId}", habitId)
            .principal(principal))
            .andExpect(status().isCreated());

        verify(habitAssignService).assignDefaultHabitForUser(eq(habitId), eq(userVO));
    }

    @Test
    void assignCustom() throws Exception {
        Long habitId = 2L;
        String json = "{"
            + "\"habitAssignPropertiesDto\":{"
            + "\"duration\":30,"
            + "\"defaultShoppingListItems\":[1,2]"
            + "},"
            + "\"friendsIdsList\":[3,4]"
            + "}";

        HabitAssignCustomPropertiesDto requestDto = mapper.readValue(json, HabitAssignCustomPropertiesDto.class);
        when(habitAssignService.assignCustomHabitForUser(eq(habitId), eq(userVO), eq(requestDto)))
            .thenReturn(Collections.singletonList(new HabitAssignManagementDto()));

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated());

        verify(habitAssignService).assignCustomHabitForUser(eq(habitId), eq(userVO), eq(requestDto));
    }

    @Test
    void updateHabitAssignDuration() throws Exception {
        Long habitAssignId = 3L;
        Integer duration = 14;
        when(habitAssignService.updateUserHabitInfoDuration(eq(habitAssignId), eq(userVO.getId()), eq(duration)))
            .thenReturn(new HabitAssignUserDurationDto());

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration", habitAssignId)
            .principal(principal)
            .param("duration", duration.toString()))
            .andExpect(status().isOk());

        verify(habitAssignService).updateUserHabitInfoDuration(eq(habitAssignId), eq(userVO.getId()), eq(duration));
    }

    @Test
    void getHabitAssign() throws Exception {
        Long habitAssignId = 4L;
        Locale locale = Locale.forLanguageTag("uk");
        when(habitAssignService.getByHabitAssignIdAndUserId(eq(habitAssignId), eq(userVO.getId()), eq("uk")))
            .thenReturn(new HabitAssignDto());

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}", habitAssignId)
            .principal(principal)
            .locale(locale))
            .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(eq(habitAssignId), eq(userVO.getId()), eq("uk"));
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquired() throws Exception {
        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(eq(userVO.getId()), eq("en")))
                .thenReturn(List.of(new HabitAssignDto()));

        mockMvc.perform(get(habitAssignLink + "/allForCurrentUser")
                        .principal(principal)
                        .locale(Locale.ENGLISH))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(eq(userVO.getId()), eq("en"));
    }

    @Test
    void getUserShoppingAndCustomShoppingLists() throws Exception {
        Long habitAssignId = 5L;
        when(habitAssignService.getUserShoppingAndCustomShoppingLists(eq(userVO.getId()), eq(habitAssignId), eq("en")))
            .thenReturn(new UserShoppingAndCustomShoppingListsDto());

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService)
            .getUserShoppingAndCustomShoppingLists(eq(userVO.getId()), eq(habitAssignId), eq("en"));
    }

    @Test
    void updateUserAndCustomShoppingLists() throws Exception {
        Long habitAssignId = 6L;
        String json = "{\"userShoppingListItemDto\":[],\"customShoppingListItemDto\":[]}";
        UserShoppingAndCustomShoppingListsDto listsDto =
            mapper.readValue(json, UserShoppingAndCustomShoppingListsDto.class);

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
            .principal(principal)
            .locale(Locale.ENGLISH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(
            eq(userVO.getId()), eq(habitAssignId), eq(listsDto), eq("en"));
    }

    @Test
    void getListOfUserAndCustomShoppingListsInprogress() throws Exception {
        when(habitAssignService.getListOfUserAndCustomShoppingListsWithStatusInprogress(eq(userVO.getId()), eq("en")))
                .thenReturn(List.of(new UserShoppingAndCustomShoppingListsDto()));

        mockMvc.perform(get(habitAssignLink + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal)
                        .locale(Locale.ENGLISH))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(eq(userVO.getId()), eq("en"));
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquired() throws Exception {
        Long habitId = 7L;
        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(eq(habitId), eq("en")))
            .thenReturn(List.of(new HabitAssignDto()));

        mockMvc.perform(get(habitAssignLink + "/{habitId}/all", habitId)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(eq(habitId), eq("en"));
    }

    @Test
    void getHabitAssignByHabitId() throws Exception {
        Long habitId = 8L;
        when(habitAssignService.findHabitAssignByUserIdAndHabitId(eq(userVO.getId()), eq(habitId), eq("en")))
            .thenReturn(new HabitAssignDto());

        mockMvc.perform(get(habitAssignLink + "/{habitId}/active", habitId)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(eq(userVO.getId()), eq(habitId), eq("en"));
    }

    @Test
    void getUsersHabitByHabitAssignId() throws Exception {
        Long habitAssignId = 9L;
        when(habitAssignService.findHabitByUserIdAndHabitAssignId(eq(userVO.getId()), eq(habitAssignId), eq("en")))
            .thenReturn(new HabitDto());

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/more", habitAssignId)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(eq(userVO.getId()), eq(habitAssignId), eq("en"));
    }

    @Test
    void updateAssignByHabitId() throws Exception {
        Long habitAssignId = 10L;
        String json = "{\"status\":\"ACQUIRED\"}";
        HabitAssignStatDto requestDto = mapper.readValue(json, HabitAssignStatDto.class);
        when(habitAssignService.updateStatusByHabitAssignId(eq(habitAssignId), eq(requestDto)))
            .thenReturn(new HabitAssignManagementDto());

        mockMvc.perform(patch(habitAssignLink + "/{habitAssignId}", habitAssignId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(eq(habitAssignId), eq(requestDto));
    }

    @Test
    void enrollHabit() throws Exception {
        Long habitAssignId = 11L;
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(habitAssignService.enrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date), eq("en")))
            .thenReturn(new HabitAssignDto());

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date), eq("en"));
    }

    @Test
    void unenrollHabit() throws Exception {
        Long habitAssignId = 12L;
        LocalDate date = LocalDate.of(2025, 2, 2);
        when(habitAssignService.unenrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date)))
            .thenReturn(new HabitAssignDto());

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date));
    }

    @Test
    void getInprogressHabitAssignOnDate() throws Exception {
        LocalDate date = LocalDate.of(2025, 3, 3);
        when(habitAssignService.findInprogressHabitAssignsOnDate(eq(userVO.getId()), eq(date), eq("en")))
            .thenReturn(List.of(new HabitAssignDto()));

        mockMvc.perform(get(habitAssignLink + "/active/{date}", date)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(eq(userVO.getId()), eq(date), eq("en"));
    }

    @Test
    void getHabitAssignBetweenDates() throws Exception {
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to = LocalDate.of(2025, 4, 30);
        when(habitAssignService.findHabitAssignsBetweenDates(eq(userVO.getId()), eq(from), eq(to), eq("en")))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get(habitAssignLink + "/activity/{from}/to/{to}", from, to)
            .principal(principal)
            .locale(Locale.ENGLISH))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(eq(userVO.getId()), eq(from), eq(to), eq("en"));
    }

    @Test
    void cancelHabitAssign() throws Exception {
        Long habitId = 13L;
        when(habitAssignService.cancelHabitAssign(eq(habitId), eq(userVO.getId())))
            .thenReturn(HabitAssignDto.builder().status(HabitAssignStatus.CANCELLED).build());

        mockMvc.perform(patch(habitAssignLink + "/cancel/{habitId}", habitId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(eq(habitId), eq(userVO.getId()));
    }

    @Test
    void deleteHabitAssign() throws Exception {
        Long habitAssignId = 14L;

        mockMvc.perform(delete(habitAssignLink + "/delete/{habitAssignId}", habitAssignId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(eq(habitAssignId), eq(userVO.getId()));
    }

    @Test
    void updateShoppingListStatus() throws Exception {
        String json = "{"
            + "\"habitAssignId\":1,"
            + "\"userShoppingListItemId\":2,"
            + "\"userShoppingListAdvanceDto\":[]"
            + "}";
        UpdateUserShoppingListDto requestDto = mapper.readValue(json, UpdateUserShoppingListDto.class);

        mockMvc.perform(put(habitAssignLink + "/saveShoppingListForHabitAssign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(eq(requestDto));
    }

    @Test
    void updateProgressNotificationHasDisplayed() throws Exception {
        Long habitAssignId = 15L;

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(eq(habitAssignId), eq(userVO.getId()));
    }
}
