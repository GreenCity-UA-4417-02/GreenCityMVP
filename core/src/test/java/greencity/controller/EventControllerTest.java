package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
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
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ErrorAttributes errorAttributes;

    @InjectMocks
    private EventController eventController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final Principal principal = ModelUtils.getPrincipal();

    @BeforeEach
    void setup() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(new HashMap<>(Map.of(
                        "message", "error",
                        "timestamp", "2026-02-08T14:04:12Z",
                        "path", "/events",
                        "status", 404
                )));

        this.mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void createEvent_Success() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        String json = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile requestPart = new MockMultipartFile("requestDto", "", "application/json", json.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("images", "test.jpg", "image/jpeg", "content".getBytes());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(eventService.createEvent(any(), any(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(multipart("/events")
                        .file(requestPart)
                        .file(imagePart)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(eventService).createEvent(eq(requestDto), any(), eq(userVO.getId()));
    }

    @Test
    void createEvent_BadRequest_InvalidDto() throws Exception {
        CreateEventRequestDto invalidDto = new CreateEventRequestDto(
                "", null, "short", null, null, null, null, true
        );

        String json = objectMapper.writeValueAsString(invalidDto);
        MockMultipartFile requestPart = new MockMultipartFile("requestDto", "", "application/json", json.getBytes());

        mockMvc.perform(multipart("/events")
                        .file(requestPart)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_NotFound() throws Exception {
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile requestPart = new MockMultipartFile("requestDto", "", "application/json", json.getBytes());

        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUserVO());
        when(eventService.createEvent(any(), any(), anyLong()))
                .thenThrow(new NotFoundException("Organizer not found"));

        mockMvc.perform(multipart("/events")
                        .file(requestPart)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }
}