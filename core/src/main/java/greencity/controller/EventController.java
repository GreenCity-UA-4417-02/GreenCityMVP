package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "event-controller")
public class EventController {
    private final EventService eventService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Create a new event",
        description = "Create event according to Acceptance Criteria requirements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    public ResponseEntity<EventResponseDto> createEvent(
        @RequestPart("requestDto") @Valid CreateEventRequestDto createEventRequestDto,
        @RequestPart(value = "images", required = false) MultipartFile[] images,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        EventResponseDto createdEvent = eventService.createEvent(createEventRequestDto, images, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
}