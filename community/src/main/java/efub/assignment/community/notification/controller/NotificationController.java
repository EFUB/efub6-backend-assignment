package efub.assignment.community.notification.controller;

import efub.assignment.community.notification.dto.response.NotificationListResponseDto;
import efub.assignment.community.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/members/{memberId}/notifications")
    public ResponseEntity<NotificationListResponseDto> getNotifications(
            @PathVariable("memberId") Long memberId
    ) {
        NotificationListResponseDto responseDto = notificationService.getNotifications(memberId);
        return ResponseEntity.ok(responseDto);
    }
}