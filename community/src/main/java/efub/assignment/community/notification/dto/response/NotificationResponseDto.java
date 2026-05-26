package efub.assignment.community.notification.dto.response;

import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        NotificationType type,
        String boardName,
        String content,
        LocalDateTime createdAt
) {
    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(
                notification.getType(),
                notification.getBoardName(),
                notification.getContent(),
                notification.getCreatedAt()
        );
    }
}