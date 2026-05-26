package efub.assignment.community.notification.controller;

import efub.assignment.community.notification.dto.response.NotificationListResponse;
import efub.assignment.community.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<NotificationListResponse> getNotifications(
            @RequestHeader("Auth-id") Long memberId
    ) {
        NotificationListResponse response =
                notificationService.getNotifications(memberId);

        return ResponseEntity.ok(response);
    }
}