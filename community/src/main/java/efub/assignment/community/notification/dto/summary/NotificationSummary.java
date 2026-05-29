package efub.assignment.community.notification.dto.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationSummary {

    private Long notificationId;

    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String boardName;

    private String content;

    private LocalDateTime createdAt;

    public static NotificationSummary from(Notification notification) {
        return NotificationSummary.builder()
                .notificationId(notification.getNotificationId())
                .type(convertType(notification))
                .boardName(notification.getBoardName())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private static String convertType(Notification notification) {
        return switch (notification.getType()) {
            case COMMENT -> "댓글";
            case MESSAGE_ROOM -> "쪽지방";
        };
    }
}