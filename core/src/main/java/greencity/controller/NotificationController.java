package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) NotificationOrigin origin,
            @Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageable) {
        return notificationService.getAllNotificationsForUser(userVO.getId(), origin, pageable);
    }
}
