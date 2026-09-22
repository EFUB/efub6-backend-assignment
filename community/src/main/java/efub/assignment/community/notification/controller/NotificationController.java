package efub.assignment.community.notification.controller;

import efub.assignment.community.global.security.AuthenticatedMember;
import efub.assignment.community.notification.dto.response.NotificationListResponseDto;
import efub.assignment.community.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<NotificationListResponseDto> getNotifications(
            @AuthenticationPrincipal AuthenticatedMember authenticatedMember
    ) {
        NotificationListResponseDto responseDto =
                notificationService.getNotifications(authenticatedMember.memberId());

        return ResponseEntity.ok(responseDto);
    }
}
