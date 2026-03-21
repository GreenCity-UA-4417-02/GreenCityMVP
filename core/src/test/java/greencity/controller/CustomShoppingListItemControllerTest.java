package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
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
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomShoppingListItemControllerTest {

    private static final String CUSTOM_SHOPPING_LIST_ITEM_LINK = "/custom/shopping-list-items";
    private final Principal principal = getPrincipal();
    private MockMvc mockMvc;

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(customShoppingListItemController)
            .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
            .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
            .build();
    }

    @Test
    void getAllAvailableCustomShoppingListItems_isOk() throws Exception {
        Long userId = 1L;
        Long habitId = 1L;
        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(1L, "Item", ShoppingListItemStatus.ACTIVE);
        
        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId))
            .thenReturn(List.of(responseDto));

        mockMvc.perform(get(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/{habitId}", userId, habitId)
                .principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].text").value("Item"))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(customShoppingListItemService).findAllAvailableCustomShoppingListItems(userId, habitId);
    }
    
    @Test
    void saveUserCustomShoppingListItems_isCreated() throws Exception {
        Long userId = 1L;
        Long habitAssignId = 1L;
        CustomShoppingListItemSaveRequestDto saveRequestDto = new CustomShoppingListItemSaveRequestDto("text");
        BulkSaveCustomShoppingListItemDto bulkSaveDto = new BulkSaveCustomShoppingListItemDto(List.of(saveRequestDto));
        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(1L, "text", ShoppingListItemStatus.ACTIVE);

        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.save(any(BulkSaveCustomShoppingListItemDto.class), eq(userId), eq(habitAssignId)))
            .thenReturn(List.of(responseDto));

        mockMvc.perform(post(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/{habitAssignId}/custom-shopping-list-items", userId, habitAssignId)
                .principal(principal)
                .content(objectMapper.writeValueAsString(bulkSaveDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].text").value("text"))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(customShoppingListItemService).save(any(BulkSaveCustomShoppingListItemDto.class), eq(userId), eq(habitAssignId));
    }

    @Test
    void updateItemStatus_isOk() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;
        String itemStatus = "DONE";
        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(itemId, "Item", ShoppingListItemStatus.DONE);

        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus)).thenReturn(responseDto);

        mockMvc.perform(patch(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal)
                .param("itemId", itemId.toString())
                .param("status", itemStatus))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemId))
            .andExpect(jsonPath("$.status").value(itemStatus));

        verify(customShoppingListItemService).updateItemStatus(userId, itemId, itemStatus);
    }
    
    @Test
    void updateItemStatus_missingParam() throws Exception {
        Long userId = 1L;
        mockMvc.perform(patch(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemStatusToDone_isOk() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        when(userService.findByEmail(any())).thenReturn(getUserVO());

        mockMvc.perform(patch(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/done", userId)
                .principal(principal)
                .param("itemId", itemId.toString()))
            .andExpect(status().isOk());

        verify(customShoppingListItemService).updateItemStatusToDone(userId, itemId);
    }
    
    @Test
    void updateItemStatusToDone_missingParam() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/done", userId)
                .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    void bulkDeleteCustomShoppingListItems_isOk() throws Exception {
        Long userId = 1L;
        String ids = "1,2";

        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(List.of(1L, 2L));

        mockMvc.perform(delete(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal)
                .param("ids", ids))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value(1L))
            .andExpect(jsonPath("$[1]").value(2L));

        verify(customShoppingListItemService).bulkDelete(ids);
    }
    
    @Test
    void bulkDeleteCustomShoppingListItems_missingParam() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCustomShoppingItemsByStatus_isOk_withStatus() throws Exception {
        Long userId = 1L;
        String status = "ACTIVE";
        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(1L, "Item", ShoppingListItemStatus.ACTIVE);

        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status))
            .thenReturn(List.of(responseDto));

        mockMvc.perform(get(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal)
                .param("status", status))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(userId, status);
    }

    @Test
    void getAllCustomShoppingItemsByStatus_isOk_withoutStatus() throws Exception {
        Long userId = 1L;
        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(1L, "Item", ShoppingListItemStatus.ACTIVE);

        when(userService.findByEmail(any())).thenReturn(getUserVO());
        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, null))
            .thenReturn(List.of(responseDto));

        mockMvc.perform(get(CUSTOM_SHOPPING_LIST_ITEM_LINK + "/{userId}/custom-shopping-list-items", userId)
                .principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(userId, null);
    }
}
