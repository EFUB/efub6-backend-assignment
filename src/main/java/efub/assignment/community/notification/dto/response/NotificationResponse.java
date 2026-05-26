package efub.assignment.community.notification.dto.response;

import efub.assignment.community.notification.domain.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        String type,
        String content,
        String boardName,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getType().name(),
                notification.getContent(),
                notification.getBoardName(),
                notification.getCreatedAt()
        );
    }
}