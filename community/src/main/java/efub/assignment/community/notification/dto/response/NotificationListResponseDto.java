package efub.assignment.community.notification.dto.response;

import efub.assignment.community.notification.domain.Notification;

import java.util.List;

public record NotificationListResponseDto(
        List<NotificationResponseDto> notifications
) {
    public static NotificationListResponseDto from(List<Notification> notifications) {
        return new NotificationListResponseDto(
                notifications.stream()
                        .map(NotificationResponseDto::from)
                        .toList()
        );
    }
}