package com.example.community.notification.controller;

import com.example.community.notification.dto.response.NotificationListResponse;
import com.example.community.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping("/notifications")
    public ResponseEntity<NotificationListResponse> getNotifications(@RequestHeader("Auth-Id") Long memberId) {
        NotificationListResponse response = notificationService.getNotifications(memberId);
        return ResponseEntity.ok(response);
    }
}
