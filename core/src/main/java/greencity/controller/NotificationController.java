package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(
            @CurrentUser UserVO userVO, @PageableDefault(value = 20) Pageable pageable) {
        return notificationService.getAllNotificationsForUser(userVO.getId(), pageable);
    }
}
