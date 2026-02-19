package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * @param pageable {@link Pageable} instance.
     * @return Pageable of {@link NotificationResponseDto}.
     */
    @Operation(summary = "Find all notifications.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK)
    })
    @GetMapping
    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(
        @Parameter(hidden = true) @CurrentUser UserVO userVO,
        @Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageable) {
        return notificationService.getAllNotificationsForUser(userVO.getId(), pageable);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notification successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "401", description = "User is not authorized")
    })
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
