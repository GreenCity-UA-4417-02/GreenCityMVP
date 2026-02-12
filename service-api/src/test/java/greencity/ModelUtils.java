package greencity;

import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.event.*;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EventType;
import greencity.enums.Role;
import greencity.enums.ShoppingListItemStatus;
import greencity.message.AddEcoNewsMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ModelUtils {
    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    private static PlaceAuthorDto getPlaceAuthorDto() {
        return PlaceAuthorDto.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("taras")
            .build();
    }

    public static SendHabitNotification getSendHabitNotification() {
        return SendHabitNotification.builder()
            .email("test@gmail.com")
            .name("taras")
            .build();
    }

    public static CategoryDto getCategoryDto() {
        return CategoryDto.builder()
            .name("name")
            .parentCategoryId(1L)
            .build();
    }

    public static SendReportEmailMessage getSendReportEmailMessage() {
        return SendReportEmailMessage.builder()
            .emailNotification("notification")
            .subscribers(Collections.singletonList(getPlaceAuthorDto()))
            .build();
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE, null,
            Arrays.asList("Новини", "News", "Новины"));
    }

    public static AddEcoNewsMessage getAddEcoNewsMessage() {
        return AddEcoNewsMessage.builder()
            .addEcoNewsDtoResponse(getAddEcoNewsDtoResponse())
            .build();
    }

    public static EcoNewsForSendEmailDto getEcoNewsForSendEmailDto() {
        return EcoNewsForSendEmailDto.builder()
            .unsubscribeToken("string")
            .creationDate(ZonedDateTime.now())
            .imagePath("string")
            .author(ModelUtils.getPlaceAuthorDto())
            .text("string")
            .source("string")
            .title("string")
            .build();
    }

    public static TagUaEnDto tagUaEnDto = TagUaEnDto.builder().id(1L).nameUa("Сщціальний").nameEn("Social").build();

    public static UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        return UserShoppingListItemResponseDto.builder()
            .id(1L)
            .text("text")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
            .id(1L)
            .text("text")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    public static UserShoppingAndCustomShoppingListsDto getUserShoppingAndCustomShoppingListsDto() {
        return UserShoppingAndCustomShoppingListsDto.builder()
            .userShoppingListItemDto(List.of(getUserShoppingListItemResponseDto()))
            .customShoppingListItemDto(List.of(getCustomShoppingListItemResponseDto()))
            .build();
    }

    public static AddCustomHabitDtoRequest getAddCustomHabitDtoRequest() {
        return AddCustomHabitDtoRequest.builder()
            .complexity(1)
            .image("")
            .defaultDuration(14)
            .tagIds(Set.of(20L))
            .build();
    }

    public static AddCustomHabitDtoResponse getAddCustomHabitDtoResponse() {
        return AddCustomHabitDtoResponse.builder()
            .id(1L)
            .complexity(1)
            .image("")
            .defaultDuration(14)
            .tagIds(Set.of(20L))
            .build();
    }


    public static AddressDto getAddressDto() {
        return new AddressDto(
                50.4501, 30.5234,
                "вул. Хрещатик", "Khreshchatyk St",
                "1", "Київ", "Kyiv",
                "Київська область", "Kyiv region",
                "Україна", "Ukraine",
                "Київ, Хрещатик, 1", "Kyiv, Khreshchatyk, 1"
        );
    }

    public static EventDateDto getEventDateDto() {
        return new EventDateDto(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                false,
                getAddressDto(),
                "https://zoom.us/j/123456789"
        );
    }

    public static EventImageDto getEventImageDto() {
        return new EventImageDto("https://example.com/image.png", true);
    }

    public static CreateEventRequestDto getCreateEventRequestDto() {
        return new CreateEventRequestDto(
                "Eco Workshop",
                List.of(getEventDateDto()),
                "This is a description that must be at least twenty characters long.",
                EventType.ONLINE_OFFLINE,
                List.of(getEventImageDto()),
                1L,
                1L,
                true
        );
    }

    public static InitiativeTypeDto getInitiativeTypeDto() {
        return new InitiativeTypeDto(1L, "Social", "Social initiative type");
    }

    public static EventCategoryDto getEventCategoryDto() {
        return new EventCategoryDto(1L, "Open Lecture", "Category for open lectures");
    }

    public static EventResponseDto getEventResponseDto() {
        return new EventResponseDto(
                1L,
                "Eco Workshop",
                "https://example.com/main-image.png",
                1L,
                "Author Name",
                LocalDate.now(),
                "Description of the event",
                true,
                EventType.ONLINE,
                List.of(getEventDateResponseDto()),
                List.of(new EventImageResponseDto(1L, "https://example.com/image.png", true)),
                getInitiativeTypeDto(),
                getEventCategoryDto(),
                10, 5, 100, 2,
                LocalDate.now()
        );
    }

    public static EventDateResponseDto getEventDateResponseDto() {
        return new EventDateResponseDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                false,
                getAddressDto(),
                "https://zoom.us/j/123456789"
        );
    }
}
