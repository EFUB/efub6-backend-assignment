package efub.assignment.community.notification.controller;

import efub.assignment.community.notification.dto.response.NotificationListResponse;
import efub.assignment.community.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 조회
    @GetMapping("/notifications")
    public ResponseEntity<NotificationListResponse> getAllNotification(@RequestHeader("Auth-Id") Long memberId) {
        NotificationListResponse response = notificationService.getAllNotifications(memberId);

        return ResponseEntity.ok(response);
    }
}
